import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.SourceDirectories
import jp.kztproject.rewardedtodo.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File
import javax.inject.Inject

/**
 * VRT(Roborazzi + Showkase の [ShowkaseParameterizedTest]) 実行時のコードカバレッジを
 * 集約計測するための convention plugin。
 *
 * VRT は app モジュールの testDebugUnitTest でのみ実行されるが、撮影対象の Composable は
 * feature/common など複数モジュールに分散している。JaCoCo エージェントはテスト中にロードされた
 * 全クラスを計測するため、app の exec には依存モジュールのカバレッジも含まれる。
 * よって [ScopedArtifacts.Scope.ALL] で依存モジュールのクラスごと集約し、ルートパッケージ
 * (jp/kztproject) で絞り込んで自前コードのみをレポート対象にする。
 *
 * NOTE: enableUnitTestCoverage を有効にすると AGP が variant 単位の coverage report タスクも
 * 自動生成するが、VRT 集約用には本 plugin が登録する `jacocoVrtReport` のみを使う。
 * 計測は Showkase が集約される debug variant に固定する。
 *
 * Configuration cache 対応のため、タスク構成ブロック内では Project のメソッド
 * (fileTree/zipTree/rootProject 等) を参照せず、注入した [ArchiveOperations] と
 * 事前にローカル変数へ退避した値のみを使う。
 */
abstract class AndroidJacocoConventionPlugin @Inject constructor(private val archiveOperations: ArchiveOperations) :
    Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("jacoco")

        extensions.configure<JacocoPluginExtension> {
            toolVersion = libs.findVersion("jacoco").get().requiredVersion
        }

        extensions.configure<ApplicationExtension> {
            buildTypes.named("debug") {
                enableUnitTestCoverage = true
            }
        }

        // Project 依存をローカル変数へ退避 (configuration cache 対応)
        val objectFactory: ObjectFactory = objects
        val archiveOps = archiveOperations
        val buildDirFile = layout.buildDirectory.get().asFile
        // ソースは行単位 HTML 表示用。カバレッジ率の計算には影響しないため best-effort で全モジュールから集める。
        val moduleSourceDirs: List<File> = rootProject.subprojects.flatMap { sub ->
            listOf(
                sub.projectDir.resolve("src/main/java"),
                sub.projectDir.resolve("src/main/kotlin"),
            )
        }

        val androidComponents = extensions.getByType<ApplicationAndroidComponentsExtension>()
        androidComponents.onVariants(androidComponents.selector().withBuildType("debug")) { variant ->
            val allJars: ListProperty<RegularFile> = objectFactory.listProperty(RegularFile::class.java)
            val allDirectories: ListProperty<Directory> = objectFactory.listProperty(Directory::class.java)

            val reportTask = tasks.register("jacocoVrtReport", JacocoReport::class) {
                group = "verification"
                description = "VRT(Roborazzi + Showkase) 実行時のコードカバレッジレポートを生成する"
                dependsOn("testDebugUnitTest")

                classDirectories.setFrom(
                    allDirectories.map { dirs ->
                        dirs.map { dir ->
                            objectFactory.fileTree().setDir(dir)
                                .include(coverageIncludes)
                                .exclude(coverageExclusions)
                        }
                    },
                    allJars.map { jars ->
                        jars.map { jar ->
                            archiveOps.zipTree(jar).matching {
                                include(coverageIncludes)
                                exclude(coverageExclusions)
                            }
                        }
                    },
                )

                sourceDirectories.setFrom(
                    moduleSourceDirs,
                    variant.sources.java.toFilePaths(),
                    variant.sources.kotlin.toFilePaths(),
                )

                // exec の出力先は AGP 設定で複数候補がありうるため build ディレクトリを横断して拾う。
                executionData.setFrom(
                    objectFactory.fileTree().setDir(buildDirFile)
                        .include(
                            "**/jacoco/testDebugUnitTest.exec",
                            "**/outputs/unit_test_code_coverage/**/*.exec",
                        ),
                )

                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    csv.required.set(false)
                }
            }

            variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                .use(reportTask)
                .toGet(
                    ScopedArtifact.CLASSES,
                    { _ -> allJars },
                    { _ -> allDirectories },
                )
        }

        tasks.withType<Test>().configureEach {
            configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }
    }

    private fun SourceDirectories.Flat?.toFilePaths(): Provider<List<String>> = this
        ?.all
        ?.map { dirs -> dirs.map { it.asFile.path } }
        ?: throw IllegalStateException("source directories are not available")
}

// 自前コードのみを対象にする (全モジュールが共有するルートパッケージ)。
private val coverageIncludes = listOf("jp/kztproject/**")

private val coverageExclusions = listOf(
    // 非UI層 (VRT では描画されないため計測対象外にしてノイズを除く)
    "**/data/**",
    "**/application/**",
    "**/domain/**",
    "**/common/database/**",
    // Android 生成物
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*_MembersInjector.class",
    // Hilt / Dagger
    "**/*_Factory.*",
    "**/*_HiltModules*.*",
    "**/Hilt_*.*",
    "**/Dagger*Component*.*",
    "**/*_GeneratedInjector.*",
    "**/hilt_aggregated_deps/**",
    // Room
    "**/*_Impl.*",
    // Showkase / KSP 生成物
    "**/*Showkase*Codegen*.*",
    "**/ShowkaseMetadata*.*",
    // Compose コンパイラ生成の lambda 集約
    "**/ComposableSingletons*.*",
    // VRT テスト本体
    "**/ShowkaseParameterizedTest*.*",
)
