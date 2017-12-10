const childProcess = require('child_process');

process.on('message', (stub) => {
  val result = childProcess.spawnSync(stub.cmd, stub.args);
  process.send(result);
});