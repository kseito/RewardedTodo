// Deterministic API fixtures; each flow still exercises the real Android transport.
const ADMIN = 'http://localhost:8080/__admin'
const cases = {
  add: [{ id: 1, name: 'Test Reward from Maestro', description: 'This is a test reward', probability_bps: 5000, need_repeat: false, version: 1 }],
  edit: [{ id: 1, name: 'Reward to Edit', description: 'Before editing', probability_bps: 2000, need_repeat: false, version: 1 }],
  delete: [{ id: 1, name: 'Reward to Delete', description: 'This reward will be deleted', probability_bps: 1000, need_repeat: false, version: 1 }],
  single: [{ id: 1, name: 'Prize Item', description: 'Test item for lottery', probability_bps: 4000, need_repeat: false, version: 1 }],
  once: [{ id: 1, name: 'One Time Prize', description: 'Disappears after winning once', probability_bps: 10000, need_repeat: false, version: 1 }],
  repeat: [{ id: 1, name: 'Repeatable Prize', description: 'Stays after winning', probability_bps: 10000, need_repeat: true, version: 1 }],
  batch: [
    { id: 1, name: 'Test Reward A', description: 'Prize A', probability_bps: 5000, need_repeat: false, version: 1 },
    { id: 2, name: 'Test Reward B', description: 'Prize B', probability_bps: 3000, need_repeat: false, version: 1 },
  ],
  insufficient: [{ id: 1, name: 'Test Reward', description: 'Prize', probability_bps: 5000, need_repeat: false, version: 1 }],
}
const rewards = cases[REWARD_CASE]
if (!rewards) throw new Error('Unknown fixture ' + REWARD_CASE)
const PATH = '/reward/api/rewards/mock-user-id'
function stub(request, body, state, next, scenario, status) {
  const mapping = { priority: 1, request: request, response: { status: status || 200, headers: { 'Content-Type': 'application/json' } } }
  if (body !== null) mapping.response.jsonBody = body
  if (state) { mapping.scenarioName = scenario || 'reward-definitions'; mapping.requiredScenarioState = state }
  if (next) mapping.newScenarioState = next
  const response = http.post(ADMIN + '/mappings', { headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(mapping) })
  if (response.status >= 400) throw new Error(response.body)
}
stub({method:'GET', urlPath:PATH}, [], 'Started')
rewards.forEach(function(reward, index) {
  const before = index === 0 ? 'Started' : 'saved-' + index
  const after = 'saved-' + (index + 1)
  stub({method:'POST', urlPath:PATH, bodyPatterns:[{matchesJsonPath: {expression:'$.name', equalTo:reward.name}}]}, reward, before, after, null, 201)
  stub({method:'GET', urlPath:PATH}, rewards.slice(0,index+1), after)
})
const lastState = 'saved-' + rewards.length
const edited = Object.assign({}, rewards[0], {need_repeat:true, version:2})
stub({method:'PUT', urlPath:PATH+'/1', bodyPatterns:[{matchesJsonPath:{expression:'$.version',equalTo:'1'}}]}, edited, lastState, 'edited')
stub({method:'GET', urlPath:PATH}, [edited], 'edited')
stub({method:'DELETE', urlPath:PATH+'/1', queryParameters:{version:{equalTo:'1'}}}, null, lastState, 'deleted', null, 204)
stub({method:'GET', urlPath:PATH}, [], 'deleted')
const isBatch = REWARD_CASE === 'batch'
const count = isBatch ? 10 : 1
const scenario = isBatch ? 'earn-ten-tickets' : 'earn-one-ticket'
const won = {id:'acquired-1', reward_definition_id:1, name:rewards[0].name, description:rewards[0].description, acquired_at:'2026-11-07T00:00:00Z', used_at:null}
const results = [{draw_number:1,type:'win',acquired_reward:won}]
for (var i=2;i<=count;i++) results.push({draw_number:i,type:'miss'})
stub({method:'POST',urlPath:'/reward/api/lotteries/mock-user-id',bodyPatterns:[{matchesJsonPath:{expression:'$.count',equalTo:String(count)}}]},
  {lottery_id:'execution-1',request_id:'fixture',count:count,consumed_points:count,available_points_after:isBatch?2:0,executed_at:'2026-11-07T00:00:00Z',results:results}, 'earned','spent',scenario)
// A separate state for the ledger allows use and app restarts to retain history.
stub({method:'GET',urlPath:'/reward/api/acquired-rewards/mock-user-id',queryParameters:{status:{equalTo:'unused'}}}, {items:[won],next_cursor:null}, 'Started',null,'inventory')
stub({method:'GET',urlPath:'/reward/api/acquired-rewards/mock-user-id',queryParameters:{status:{equalTo:'used'}}}, {items:[],next_cursor:null}, 'Started',null,'inventory')
const used = Object.assign({},won,{used_at:'2026-11-07T01:00:00Z'})
stub({method:'PUT',urlPath:'/reward/api/acquired-rewards/mock-user-id/acquired-1/use'},used,'Started','used','inventory')
stub({method:'GET',urlPath:'/reward/api/acquired-rewards/mock-user-id',queryParameters:{status:{equalTo:'unused'}}},{items:[],next_cursor:null},'used',null,'inventory')
stub({method:'GET',urlPath:'/reward/api/acquired-rewards/mock-user-id',queryParameters:{status:{equalTo:'used'}}},{items:[used],next_cursor:null},'used',null,'inventory')
// Registered last, the spent scenario takes precedence over the saved definitions.
stub({method:'GET',urlPath:PATH}, REWARD_CASE==='repeat'?rewards:(isBatch?[rewards[1]]:[]), 'spent',null,scenario)
