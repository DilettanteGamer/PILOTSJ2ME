# Export Ghidra's analysis as reviewable text. Run using analyzeHeadless.
# @category PILOTS
import codecs
import json
import os

from ghidra.app.decompiler import DecompInterface, DecompileOptions


args = getScriptArgs()
if len(args) != 1:
    raise ValueError('Expected output directory')
out = os.path.join(args[0], currentProgram.getName())
if not os.path.isdir(out):
    os.makedirs(out)


def output(name):
    return codecs.open(os.path.join(out, name), 'w', 'utf-8')


def clean(value):
    return unicode(value).replace('\t', '\\t').replace('\r', '\\r').replace('\n', '\\n')


decompiler = DecompInterface()
decompiler.setOptions(DecompileOptions())
if not decompiler.openProgram(currentProgram):
    raise RuntimeError('Decompiler failed to open program')
stats = {'functions': 0, 'decompiled': 0, 'failed': 0, 'thunks': 0, 'failures': []}
c = output('decompiled.c')
index = output('functions.tsv')
index.write(u'address\tname\tstatus\tsignature\tdiagnostic\n')
c.write(u'/* Automated Ghidra 11.3.2 C pseudocode. NOT original source or buildable C.\n'
        u' * Input: ' + unicode(currentProgram.getName()) + u'\n'
        u' * Addresses and inferred types require manual validation.\n */\n\n')
try:
    for function in currentProgram.getFunctionManager().getFunctions(True):
        monitor.checkCancelled()
        stats['functions'] += 1
        address = str(function.getEntryPoint())
        name = str(function.getName())
        if function.isThunk():
            stats['thunks'] += 1
            target = function.getThunkedFunction(True)
            diagnostic = 'Thunk to ' + str(target)
            status = 'thunk'
            c.write(u'/* ' + clean(address) + u': ' + clean(name) + u'; ' + clean(diagnostic) + u' */\n\n')
        else:
            result = decompiler.decompileFunction(function, 60, monitor)
            diagnostic = str(result.getErrorMessage() or '')
            if result.decompileCompleted() and result.getDecompiledFunction() is not None:
                stats['decompiled'] += 1
                status = 'decompiled'
                c.write(u'/* Address: ' + clean(address) + u' */\n')
                c.write(unicode(result.getDecompiledFunction().getC()) + u'\n\n')
            else:
                status = 'failed'
                stats['failed'] += 1
                stats['failures'].append({'address': address, 'name': name, 'error': diagnostic})
                c.write(u'/* DECOMPILATION FAILED: ' + clean(address) + u' ' + clean(name) + u' */\n\n')
        index.write(u'\t'.join(clean(v) for v in [address, name, status, function.getSignature(), diagnostic]) + u'\n')
finally:
    c.close()
    index.close()
    decompiler.dispose()

with output('disassembly.tsv') as f:
    f.write(u'address\tbytes\tinstruction\n')
    for instruction in currentProgram.getListing().getInstructions(True):
        raw = ''.join('%02x' % (v & 255) for v in instruction.getBytes())
        f.write(clean(instruction.getAddress()) + u'\t' + raw + u'\t' + clean(instruction) + u'\n')

with output('symbols.tsv') as f:
    f.write(u'address\tname\ttype\texternal\n')
    for symbol in currentProgram.getSymbolTable().getAllSymbols(True):
        f.write(u'\t'.join(clean(v) for v in [symbol.getAddress(), symbol.getName(True), symbol.getSymbolType(), symbol.isExternal()]) + u'\n')

with output('strings.tsv') as f:
    f.write(u'address\tghidra_value\tcp1251_interpretation\n')
    for data in currentProgram.getListing().getDefinedData(True):
        if 'string' not in str(data.getDataType().getName()).lower():
            continue
        raw = ''.join(chr(v & 255) for v in data.getBytes()).rstrip('\x00')
        decoded = raw.decode('cp1251', 'replace')
        f.write(u'\t'.join(clean(v) for v in [data.getAddress(), data.getValue(), decoded]) + u'\n')

blocks = []
for block in currentProgram.getMemory().getBlocks():
    blocks.append({'name': str(block.getName()), 'start': str(block.getStart()),
                   'size': long(block.getSize()), 'initialized': bool(block.isInitialized()),
                   'execute': bool(block.isExecute())})
stats.update({'input': str(currentProgram.getName()),
              'language': str(currentProgram.getLanguageID()),
              'compiler_spec': str(currentProgram.getCompilerSpec().getCompilerSpecID()),
              'format': str(currentProgram.getExecutableFormat()),
              'image_base': str(currentProgram.getImageBase()), 'memory_blocks': blocks,
              'tool': 'Ghidra 11.3.2 PUBLIC',
              'scope': 'Automatically discovered functions; not proof of complete code coverage'})
with output('analysis.json') as f:
    f.write(unicode(json.dumps(stats, indent=2, sort_keys=True)) + u'\n')
print('PILOTS_EXPORT_RESULT ' + json.dumps(stats, sort_keys=True))
