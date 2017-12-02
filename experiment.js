var isWin = require('os').platform().indexOf('win') > -1;

var where = isWin ? 'where' : 'whereis';

var childProcess = require('child_process');

var out = childProcess.spawnSync(where, ['lssdgasd']);

console.log(out.status);
