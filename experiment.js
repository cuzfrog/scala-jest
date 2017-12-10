var childProcess = require('child_process');

var subProcess = childProcess.fork("target/subprocess.js");

subProcess.on('message', (msg) => {
  console.log('[experiment.js]received message: ', msg);
});

console.log('experiment executed')