const childProcess = require('child_process');

//const result = childProcess.spawnSync('npm',['test','--','target/tmp/jests/mock-test-fq6RH9tEug.test.js']);
//const result = childProcess.execSync('npm test -- target/tmp/jests/mock-test-fq6RH9tEug.test.js');
const result = childProcess.spawnSync('echo',['some-value'])
console.log(result.stdout.toString());