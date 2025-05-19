find ./app/build/outputs/roborazzi -type f -name '*_compare.png' | while read -r filepath; do
  echo "$filepath"
#  gsutil cp "$filepath" gs://my-bucket/path/in/bucket/"$filepath"
done