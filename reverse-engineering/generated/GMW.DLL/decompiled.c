/* Automated Ghidra 11.3.2 C pseudocode. NOT original source or buildable C.
 * Input: GMW.DLL
 * Addresses and inferred types require manual validation.
 */

/* Address: 1000:0000 */

/* Title:   [- LL MIDI/WAVE Driver for Win16 by Vadim Sytnikov -] 
   Format: New Executable (NE) Windows
   CRC:    00000000
   
   Program Entry Point (CS:IP):   0001:0690
   Initial Stack Pointer (SS:SP): 0000:0000
   Auto Data Segment Index:       0002
   Initial Heap Size:             0400
   Initial Stack Size:            0000
   Minimum Code Swap Size:        0000
   
   Linker Version:  17.33
   Target OS:       Windows
   Windows Version: 3.10
   
   Program Flags:     09
           Single Data
           Protected Mode
   Application Flags: 83
           Windows P.M. API
           Library Module
   Other Flags:       08
    */

void __cdecl16near FUN_1000_0000(void)

{
  uint uVar1;
  undefined2 unaff_CS;
  undefined2 uVar2;
  
                    /* Segment:    1
                       Offset:     000002c0
                       Length:     087b
                       Min Alloc:  087b
                       Flags:      0d40
                           Code
                           Fixed
                           Preload
                           Impure (Non-shareable)
                        */
  uVar1 = 0xe8;
  do {
    uVar2 = DAT_1008_00e4;
    WAVEOUTUNPREPAREHEADER(unaff_CS,0x20,uVar1,0x1008);
    uVar1 = uVar1 + 0x20;
    unaff_CS = 0x1010;
  } while (uVar1 < 0x1e8);
  WAVEOUTCLOSE(0x1010,DAT_1008_00e4,uVar2);
  return;
}



/* Address: 1000:0026 */

/* WARNING: Stack frame is not setup normally: Input value of stackpointer is not used */

undefined2 FUN_1000_0026(void)

{
  byte *pbVar1;
  undefined2 *puVar2;
  int iVar3;
  undefined2 uVar4;
  int iVar5;
  undefined4 *puVar6;
  undefined4 *puVar7;
  undefined4 *puVar8;
  undefined2 *puVar9;
  undefined4 *puVar10;
  undefined4 *puVar11;
  undefined1 *puVar12;
  undefined1 *puVar13;
  undefined1 *puVar14;
  undefined1 *puVar15;
  undefined1 *puVar16;
  undefined4 in_ESP;
  undefined4 *puVar17;
  undefined2 unaff_BP;
  undefined2 unaff_SI;
  uint uVar19;
  undefined2 unaff_DI;
  byte *pbVar20;
  undefined2 unaff_CS;
  undefined2 unaff_SS;
  undefined1 *puVar18;
  
  iVar5 = (int)in_ESP;
  *(undefined2 *)(iVar5 + -2) = unaff_BP;
  *(undefined2 *)(iVar5 + -0x1a) = unaff_SI;
  *(undefined2 *)(iVar5 + -0x1c) = unaff_DI;
  *(undefined2 *)(iVar5 + -0x16) = 1;
  *(undefined2 *)(iVar5 + -0x14) = 1;
  *(undefined2 *)(iVar5 + -0xc) = 0;
  *(undefined2 *)(iVar5 + -0xe) = 0x2b11;
  *(undefined2 *)(iVar5 + -0x10) = 0;
  *(undefined2 *)(iVar5 + -0x12) = 0x2b11;
  *(undefined2 *)(iVar5 + -10) = 1;
  *(undefined2 *)(iVar5 + -8) = 8;
  *(undefined2 *)(iVar5 + -0x1e) = 0x1008;
  *(undefined2 *)(iVar5 + -0x20) = 0xe4;
  *(undefined2 *)(iVar5 + -0x22) = 0xffff;
  *(undefined2 *)(iVar5 + -0x24) = unaff_SS;
  *(int *)(iVar5 + -0x26) = iVar5 + -0x16;
  puVar2 = (undefined2 *)*(undefined4 *)(iVar5 + 2);
  *(undefined2 *)(iVar5 + -0x28) = ((undefined2 *)puVar2)[1];
  *(undefined2 *)(iVar5 + -0x2a) = *puVar2;
  puVar17 = (undefined4 *)(CONCAT22((int)((ulong)in_ESP >> 0x10),iVar5 + -0x2a) + -4);
  *puVar17 = 0;
  puVar6 = (undefined4 *)puVar17;
  uVar4 = (undefined2)((ulong)puVar17 >> 0x10);
  puVar6[-1] = 0x10000;
  *(undefined2 *)((int)puVar6 + -6) = unaff_CS;
  puVar7 = puVar6 + -2;
  *(undefined2 *)(puVar6 + -2) = 0x73;
  iVar3 = WAVEOUTOPEN();
  puVar18 = (undefined1 *)CONCAT22(uVar4,(undefined1 *)((int)puVar7 + 0x18));
  *(int *)(iVar5 + -0x18) = iVar3;
  if (iVar3 != 0) {
    puVar17 = (undefined4 *)(puVar18 + -4);
    *puVar17 = 0;
    puVar8 = (undefined4 *)puVar17;
    uVar4 = (undefined2)((ulong)puVar17 >> 0x10);
    *(undefined2 *)((int)puVar8 + -2) = 0;
    *(undefined2 *)(puVar8 + -1) = 0x1010;
    puVar9 = (undefined2 *)((int)puVar8 + -6);
    *(undefined2 *)((int)puVar8 + -6) = 0x84;
    SNDPLAYSOUND();
    puVar9[3] = 0x1008;
    puVar9[2] = 0xe4;
    puVar9[1] = 0xffff;
    *puVar9 = unaff_SS;
    puVar9[-1] = iVar5 + -0x16;
    puVar2 = (undefined2 *)*(undefined4 *)(iVar5 + 2);
    puVar9[-2] = ((undefined2 *)puVar2)[1];
    puVar9[-3] = *puVar2;
    *(undefined4 *)((undefined2 *)CONCAT22(uVar4,puVar9 + -3) + -2) = 0;
    puVar10 = (undefined4 *)((undefined2 *)CONCAT22(uVar4,puVar9 + -3) + -2);
    puVar10[-1] = 0x10000;
    *(undefined2 *)((int)puVar10 + -6) = 0x1010;
    puVar11 = puVar10 + -2;
    *(undefined2 *)(puVar10 + -2) = 0xa8;
    uVar4 = WAVEOUTOPEN();
    puVar18 = (undefined1 *)ZEXT24((undefined1 *)((int)puVar11 + 0x18));
    *(undefined2 *)(iVar5 + -0x18) = uVar4;
  }
  if (*(int *)(iVar5 + -0x18) == 0) {
    puVar12 = (undefined1 *)puVar18;
    *(undefined2 *)(puVar12 + -2) = DAT_1008_00e4;
    *(undefined2 *)(puVar12 + -4) = 0x1008;
    *(undefined2 *)(puVar12 + -6) = 0xe6;
    *(undefined2 *)(puVar12 + -8) = 0x1010;
    puVar13 = puVar12 + -10;
    *(undefined2 *)(puVar12 + -10) = 0xbe;
    iVar3 = WAVEOUTGETID();
    puVar14 = puVar13 + 8;
    puVar16 = puVar13 + 8;
    if (iVar3 == 0) {
      pbVar20 = (byte *)&DAT_1008_00f8;
      uVar19 = 0xe8;
      uVar4 = *(undefined2 *)(iVar5 + 2);
      *(undefined2 *)(iVar5 + -4) = *(undefined2 *)(iVar5 + 4);
      *(undefined2 *)(iVar5 + -6) = uVar4;
      do {
        (pbVar20 + 2)[0] = 0;
        (pbVar20 + 2)[1] = 0;
        pbVar20[0] = 1;
        pbVar20[1] = 0;
        uVar4 = *(undefined2 *)(iVar5 + -6);
        *(undefined2 *)(pbVar20 + -0xe) = *(undefined2 *)(iVar5 + -4);
        *(undefined2 *)(pbVar20 + -0x10) = uVar4;
        pbVar1 = pbVar20 + -0xc;
        pbVar1[0] = 0;
        pbVar1[1] = 8;
        pbVar1[2] = 0;
        pbVar1[3] = 0;
        *(undefined2 *)(puVar14 + -2) = DAT_1008_00e4;
        *(undefined2 *)(puVar14 + -4) = 0x1008;
        *(uint *)(puVar14 + -6) = uVar19;
        *(undefined2 *)(puVar14 + -8) = 0x20;
        *(undefined2 *)(puVar14 + -10) = 0x1010;
        puVar15 = puVar14 + -0xc;
        *(undefined2 *)(puVar14 + -0xc) = 0xfe;
        iVar3 = WAVEOUTPREPAREHEADER();
        puVar14 = puVar15 + 10;
        puVar16 = puVar15 + 10;
        *(int *)(iVar5 + -0x18) = iVar3;
        *pbVar20 = *pbVar20 | 1;
        if (iVar3 != 0) break;
        pbVar20 = pbVar20 + 0x20;
        uVar19 = uVar19 + 0x20;
        *(int *)(iVar5 + -6) = *(int *)(iVar5 + -6) + 0x800;
      } while (uVar19 < 0x1e8);
      if (*(int *)(iVar5 + -0x18) == 0) {
        return 1;
      }
    }
    *(undefined2 *)(puVar16 + -2) = 300;
    FUN_1000_0000();
  }
  return 0;
}



/* Address: 1000:0136 */

uint __cdecl16near FUN_1000_0136(void)

{
  byte *pbVar1;
  
  pbVar1 = (byte *)&DAT_1008_00f8;
  do {
    if ((*pbVar1 & 1) != 0) {
      return (uint)(pbVar1 + -0xf8) >> 5;
    }
    pbVar1 = pbVar1 + 0x20;
  } while (pbVar1 < (byte *)&DAT_1008_01f8);
  WAVEOUTRESET();
  return 0;
}



/* Address: 1000:0160 */

bool __cdecl16near FUN_1000_0160(void)

{
  uint uVar1;
  int iVar2;
  undefined2 unaff_CS;
  undefined2 uVar3;
  
  uVar3 = 0x1008;
  uVar1 = FUN_1000_0136(0x1008,DAT_1008_00e4);
  iVar2 = WAVEOUTWRITE(unaff_CS,0x20,(uVar1 & 0xff) * 0x20 + 0xe8,uVar3);
  return iVar2 == 0;
}



/* Address: 1000:017e */

bool __cdecl16near FUN_1000_017e(void)

{
  int iVar1;
  
  iVar1 = WAVEOUTRESET();
  return iVar1 == 0;
}



/* Address: 1000:018e */

bool __cdecl16near FUN_1000_018e(void)

{
  int iVar1;
  
  iVar1 = WAVEOUTPAUSE();
  return iVar1 == 0;
}



/* Address: 1000:019e */

bool __cdecl16near FUN_1000_019e(void)

{
  int iVar1;
  
  iVar1 = WAVEOUTRESTART();
  return iVar1 == 0;
}



/* Address: 1000:01ae */

uint __cdecl16near FUN_1000_01ae(void)

{
  undefined2 unaff_CS;
  undefined2 unaff_SS;
  uint local_6;
  uint local_4;
  
  WAVEOUTGETVOLUME(unaff_CS,&local_6,unaff_SS);
  if (local_4 < local_6) {
    local_4 = local_6;
  }
  return local_4 >> 8;
}



/* Address: 1000:01da */

undefined2 FUN_1000_01da(char param_1)

{
  int iVar1;
  undefined1 extraout_AH;
  undefined1 uVar2;
  undefined2 unaff_CS;
  undefined2 uVar3;
  
  uVar3 = DAT_1008_00e6;
  iVar1 = WAVEOUTSETVOLUME(unaff_CS,CONCAT11(param_1,0xff),CONCAT11(param_1,0xff));
  uVar2 = 0;
  if (param_1 == '\0') {
    WAVEOUTRESET(0x1010,DAT_1008_00e4,uVar3);
    uVar2 = extraout_AH;
  }
  return CONCAT11(uVar2,iVar1 == 0);
}



/* Address: 1000:0228 */

void __cdecl16near FUN_1000_0228(void)

{
  undefined2 unaff_CS;
  
  if (DAT_1008_01ec != 0) {
    unaff_CS = 0x1010;
    TIMEKILLEVENT();
    DAT_1008_01ec = 0;
  }
  MIDIOUTRESET(unaff_CS,DAT_1008_01e8);
  return;
}



/* Address: 1000:0248 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

uint __cdecl16near FUN_1000_0248(void)

{
  byte bVar1;
  byte *pbVar2;
  uint uVar3;
  
  bVar1 = *_DAT_1008_01fc;
  uVar3 = (uint)bVar1;
  pbVar2 = DAT_1008_01fc;
  _DAT_1008_01fc = (byte *)CONCAT22(_DAT_1008_01fe,DAT_1008_01fc + 1);
  if ((bVar1 & 0x80) != 0) {
    uVar3 = uVar3 & 0x7f | (uint)*_DAT_1008_01fc << 7;
    _DAT_1008_01fc = (byte *)CONCAT22(_DAT_1008_01fe,pbVar2 + 2);
  }
  return uVar3;
}



/* Address: 1000:03e0 */

void __cdecl16near FUN_1000_03e0(void)

{
  undefined2 unaff_CS;
  
  FUN_1000_0228();
  if (DAT_1008_0202 != 0) {
    unaff_CS = 0x1010;
    TIMEENDPERIOD();
    DAT_1008_0202 = 0;
  }
  if (DAT_1008_01e8 != 0) {
    MIDIOUTCLOSE(unaff_CS,DAT_1008_01e8);
    DAT_1008_01e8 = 0;
  }
  return;
}



/* Address: 1000:0410 */

/* WARNING: Stack frame is not setup normally: Input value of stackpointer is not used */

undefined2 __cdecl16near FUN_1000_0410(void)

{
  int iVar1;
  uint uVar2;
  int iVar3;
  undefined4 *puVar4;
  undefined2 *puVar6;
  undefined2 *puVar7;
  undefined4 in_ESP;
  long lVar9;
  undefined2 unaff_BP;
  undefined2 unaff_CS;
  undefined2 unaff_SS;
  undefined4 *puVar5;
  undefined2 *puVar8;
  
  iVar3 = (int)in_ESP;
  *(undefined2 *)(iVar3 + -2) = unaff_BP;
  *(undefined2 *)(iVar3 + -8) = 0x1008;
  *(undefined2 *)(iVar3 + -10) = 0x1e8;
  lVar9 = CONCAT22((int)((ulong)in_ESP >> 0x10),iVar3 + -0xc);
  *(undefined2 *)(iVar3 + -0xc) = 0xffff;
  *(undefined4 *)(lVar9 + -4) = 0;
  *(undefined4 *)(lVar9 + -8) = 0;
  *(undefined4 *)(lVar9 + -0xc) = 0;
  puVar4 = (undefined4 *)(undefined4 *)(lVar9 + -0xc);
  *(undefined2 *)((int)puVar4 + -2) = unaff_CS;
  puVar5 = puVar4 + -1;
  *(undefined2 *)(puVar4 + -1) = 0x42a;
  iVar1 = MIDIOUTOPEN();
  if (iVar1 == 0) {
    DAT_1008_0202 = 0;
    DAT_1008_0201 = 1;
    *(undefined2 *)((int)puVar5 + 0x12) = DAT_1008_01e8;
    *(undefined2 *)((int)puVar5 + 0x10) = 0x1008;
    *(undefined2 *)((int)puVar5 + 0xe) = 0x1ea;
    *(undefined2 *)((int)puVar5 + 0xc) = 0x1010;
    puVar6 = (undefined2 *)((int)puVar5 + 10);
    *(undefined2 *)((int)puVar5 + 10) = 0x446;
    iVar1 = MIDIOUTGETID();
    puVar7 = puVar6 + 4;
    if (iVar1 == 0) {
      puVar6[3] = unaff_SS;
      puVar6[2] = iVar3 + -6;
      puVar6[1] = 4;
      *puVar6 = 0x1010;
      puVar8 = puVar6 + -1;
      puVar6[-1] = 0x456;
      iVar1 = TIMEGETDEVCAPS();
      puVar7 = (undefined2 *)((int)puVar8 + 8);
      if (iVar1 == 0) {
        uVar2 = *(uint *)(iVar3 + -6);
        if (uVar2 < 0xb) {
          uVar2 = 10;
        }
        if (uVar2 < *(uint *)(iVar3 + -4)) {
          if (*(uint *)(iVar3 + -6) < 0xb) {
            DAT_1008_0202 = 10;
          }
          else {
            DAT_1008_0202 = *(undefined2 *)(iVar3 + -6);
          }
        }
        else {
          DAT_1008_0202 = *(undefined2 *)(iVar3 + -4);
        }
        *(undefined2 *)((int)puVar8 + 6) = DAT_1008_0202;
        *(undefined2 *)((int)puVar8 + 4) = 0x1010;
        puVar7 = (undefined2 *)((int)puVar8 + 2);
        *(undefined2 *)((int)puVar8 + 2) = 0x487;
        iVar1 = TIMEBEGINPERIOD();
        if (iVar1 == 0) {
          return 1;
        }
      }
    }
    *(undefined2 *)((int)puVar7 + -2) = 0x494;
    FUN_1000_03e0();
  }
  return 0;
}



/* Address: 1000:049a */

/* WARNING: Stack frame is not setup normally: Input value of stackpointer is not used */
/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

bool FUN_1000_049a(void)

{
  int iVar1;
  undefined2 *puVar2;
  undefined2 *puVar3;
  undefined4 *puVar4;
  undefined1 *puVar5;
  undefined4 in_ESP;
  undefined2 uVar6;
  undefined2 unaff_BP;
  undefined2 unaff_CS;
  undefined2 unaff_SS;
  bool bVar7;
  
  iVar1 = (int)in_ESP;
  uVar6 = (undefined2)((ulong)in_ESP >> 0x10);
  *(undefined2 *)(iVar1 + -2) = unaff_BP;
  puVar2 = (undefined2 *)(iVar1 + -4);
  *(undefined2 *)(iVar1 + -4) = 0x4a0;
  FUN_1000_0228();
  puVar3 = puVar2 + 1;
  _DAT_1008_01fe = *(undefined2 *)(iVar1 + 4);
  DAT_1008_01fc = (char *)(*(int *)(iVar1 + 2) + 4);
  if (*DAT_1008_01fc == -8) {
    DAT_1008_01fc = (char *)(*(int *)(iVar1 + 2) + 5);
    *puVar2 = 0x4c1;
    FUN_1000_0248();
    puVar3 = (undefined2 *)((int)puVar2 + 2);
    DAT_1008_01fc = (char *)((int)DAT_1008_01fc + 1);
  }
  DAT_1008_01fa = _DAT_1008_01fe;
  DAT_1008_01f8 = DAT_1008_01fc;
  DAT_1008_01f4 = 1;
  DAT_1008_01f6 = 1;
  DAT_1008_01f2 = 0;
  *(undefined2 *)((int)puVar3 + -2) = 10;
  *(undefined2 *)((int)puVar3 + -4) = DAT_1008_0202;
  *(undefined2 *)((int)puVar3 + -6) = 0x1000;
  *(undefined2 *)((int)puVar3 + -8) = 0x394;
  *(undefined4 *)((undefined1 *)CONCAT22(uVar6,(undefined1 *)((int)puVar3 + -8)) + -4) = 0;
  puVar4 = (undefined4 *)((undefined1 *)CONCAT22(uVar6,(undefined1 *)((int)puVar3 + -8)) + -4);
  *(undefined2 *)((int)puVar4 + -2) = 1;
  *(undefined2 *)(puVar4 + -1) = unaff_CS;
  puVar5 = (undefined1 *)((int)puVar4 + -6);
  *(undefined2 *)((int)puVar4 + -6) = 0x4f7;
  DAT_1008_01ec = TIMESETEVENT();
  bVar7 = DAT_1008_01ec != 0;
  if (!bVar7) {
    *(undefined2 *)(puVar5 + 0xe) = 0x504;
    FUN_1000_0228();
  }
  return bVar7;
}



/* Address: 1000:050a */

undefined1 __cdecl16near FUN_1000_050a(void)

{
  FUN_1000_0228();
  return 1;
}



/* Address: 1000:0510 */

uint __cdecl16near FUN_1000_0510(void)

{
  undefined2 unaff_CS;
  undefined2 unaff_SS;
  uint local_6;
  uint local_4;
  
  MIDIOUTGETVOLUME(unaff_CS,&local_6,unaff_SS);
  if (local_4 < local_6) {
    local_4 = local_6;
  }
  return local_4 >> 8;
}



/* Address: 1000:053c */

undefined2 FUN_1000_053c(char param_1)

{
  int iVar1;
  undefined1 extraout_AH;
  undefined1 uVar2;
  undefined2 unaff_CS;
  undefined2 uVar3;
  
  DAT_1008_0201 = param_1;
  uVar3 = DAT_1008_01ea;
  iVar1 = MIDIOUTSETVOLUME(unaff_CS,CONCAT11(param_1,0xff),CONCAT11(param_1,0xff));
  uVar2 = 0;
  if (param_1 == '\0') {
    MIDIOUTRESET(0x1010,DAT_1008_01e8,uVar3);
    uVar2 = extraout_AH;
  }
  return CONCAT11(uVar2,iVar1 == 0);
}



/* Address: 1000:058c */

undefined2 __stdcall16far FUN_1000_058c(void)

{
  return 1;
}



/* Address: 1000:0596 */

undefined2 __stdcall16far WEP(void)

{
  FUN_1000_0000(0x1008);
  FUN_1000_03e0();
  return 1;
}



/* Address: 1000:05ae */

byte __stdcall16far UTPROC(uint param_1,uint param_2,undefined4 param_3)

{
  byte bVar1;
  byte bVar2;
  code *pcVar3;
  
  bVar1 = 0;
  bVar2 = 0;
  if (param_2 == param_1 < 0x200) {
    switch(param_1) {
    case 0x200:
      if (DAT_1008_0204 == 0) {
        bVar2 = FUN_1000_0026(param_3);
        bVar1 = FUN_1000_0410();
        DAT_1008_0204 = bVar2 | bVar1;
        bVar2 = DAT_1008_0204;
      }
      break;
    case 0x201:
      if (DAT_1008_0204 != 0) {
        FUN_1000_0000();
        FUN_1000_03e0();
        DAT_1008_0204 = 0;
        bVar2 = bVar1;
      }
      break;
    case 0x202:
      bVar2 = FUN_1000_0160();
      break;
    case 0x203:
      bVar2 = FUN_1000_017e();
      break;
    case 0x204:
      bVar2 = FUN_1000_01ae();
      break;
    case 0x205:
      bVar2 = FUN_1000_0136();
      break;
    case 0x206:
      bVar2 = FUN_1000_018e();
      break;
    case 0x207:
      bVar2 = FUN_1000_019e();
      break;
    case 0x208:
      bVar2 = FUN_1000_049a(param_3);
      break;
    case 0x209:
      bVar2 = FUN_1000_050a();
      break;
    case 0x20a:
      bVar2 = FUN_1000_0510(0x1000,0x1008);
      break;
    default:
      goto switchD_1000_05d8_default;
    }
  }
  else {
switchD_1000_05d8_default:
    if (param_1 < 0x100) {
      pcVar3 = (code *)0x1da;
    }
    else {
      pcVar3 = (code *)0x53c;
    }
    bVar2 = (*pcVar3)();
  }
  return bVar2;
}



/* 1000:0690: entry; Thunk to FUN_1000_06b0 */

/* Address: 1000:06b0 */

void __cdecl16far FUN_1000_06b0(void)

{
  int iVar1;
  int in_CX;
  undefined2 unaff_SI;
  undefined2 unaff_DI;
  undefined2 unaff_ES;
  undefined2 unaff_CS;
  undefined2 uVar2;
  
  DAT_1008_0038 = 0x2002;
  DAT_1008_0043 = 2;
  DAT_1008_0042 = 1;
  if (in_CX != 0) {
    uVar2 = 0x1008;
    iVar1 = LOCALINIT(unaff_CS,in_CX,0);
    if ((iVar1 == 0) || (iVar1 = func_0x100007e0(0x1010,uVar2), iVar1 == 0)) {
      return;
    }
  }
  uVar2 = 0x1008;
  DAT_1008_0032 = unaff_DI;
  DAT_1008_0034 = unaff_DI;
  FUN_1000_0700();
  DAT_1008_0036 = DAT_1008_0036 + '\x01';
  FUN_1000_058c(unaff_SI,unaff_ES,in_CX,uVar2,unaff_DI);
  return;
}



/* Address: 1000:0700 */

void __cdecl16far FUN_1000_0700(void)

{
  int iVar1;
  int unaff_BP;
  int iVar2;
  undefined2 unaff_SS;
  undefined4 uVar3;
  
  iVar2 = unaff_BP + 1;
  uVar3 = GETVERSION();
  DAT_1008_0044 = (undefined2)((ulong)uVar3 >> 0x10);
  DAT_1008_0046 = CONCAT11((char)uVar3,(char)((ulong)uVar3 >> 8));
  DAT_1008_0040 = CONCAT11((char)((ulong)uVar3 >> 0x10),(char)((ulong)uVar3 >> 0x18));
  DAT_1008_003e = (undefined2)((ulong)*(undefined4 *)DAT_1008_0068 >> 0x10);
  DAT_1008_003c = (undefined2)*(undefined4 *)DAT_1008_0068;
  DAT_1008_0048 = *(int *)0xa + 0x100;
  FUN_1000_0756();
  iVar1 = FUN_1000_0820();
  if (iVar1 == -1) {
    FUN_1000_07cd(iVar2);
    return;
  }
  return;
}



/* Address: 1000:0746 */

void FUN_1000_0746(void)

{
  int *piVar1;
  int *piVar2;
  int *piVar3;
  
  DOS3CALL();
  piVar3 = (int *)&DAT_1008_00e4;
  while ((int *)&DAT_1008_00e4 < piVar3) {
    piVar2 = piVar3 + -2;
    piVar1 = piVar3 + -1;
    piVar3 = piVar2;
    if (*piVar2 != 0 || *piVar1 != 0) {
      (*(code *)*piVar2)(0x1010);
    }
  }
  DAT_1008_0070 = 0x21;
  piVar3 = (int *)&DAT_1008_00e4;
  while ((int *)&DAT_1008_00e4 < piVar3) {
    piVar2 = piVar3 + -2;
    piVar1 = piVar3 + -1;
    piVar3 = piVar2;
    if (*piVar2 != 0 || *piVar1 != 0) {
      (*(code *)*piVar2)(0x1010);
    }
  }
  return;
}



/* Address: 1000:0756 */

void __cdecl16near FUN_1000_0756(void)

{
  int *piVar1;
  int *piVar2;
  int *piVar3;
  
  piVar3 = (int *)&DAT_1008_00e4;
  while ((int *)&DAT_1008_00e4 < piVar3) {
    piVar2 = piVar3 + -2;
    piVar1 = piVar3 + -1;
    piVar3 = piVar2;
    if (*piVar2 != 0 || *piVar1 != 0) {
      (*(code *)*piVar2)();
    }
  }
  DAT_1008_0070 = 0x21;
  piVar3 = (int *)&DAT_1008_00e4;
  while ((int *)&DAT_1008_00e4 < piVar3) {
    piVar2 = piVar3 + -2;
    piVar1 = piVar3 + -1;
    piVar3 = piVar2;
    if (*piVar2 != 0 || *piVar1 != 0) {
      (*(code *)*piVar2)();
    }
  }
  return;
}



/* DECOMPILATION FAILED: 1000:07c7 FUN_1000_07c7 */

/* DECOMPILATION FAILED: 1000:07cd FUN_1000_07cd */

/* Address: 1000:0820 */

void __cdecl16far FUN_1000_0820(void)

{
  if (DAT_1008_02a6 != 0 || DAT_1008_02a4 != (code *)0x0) {
    (*DAT_1008_02a4)();
  }
  return;
}



/* Address: 1000:0840 */

/* WARNING: Unable to track spacebase fully for stack */

void __stdcall16far FUN_1000_0840(undefined4 param_1)

{
  int iVar1;
  undefined2 unaff_CS;
  undefined2 unaff_SS;
  
  iVar1 = *(int *)0xe;
  *(undefined2 *)(iVar1 + -0x802) = 8;
  *(undefined2 *)(iVar1 + -0x804) = 6;
  *(undefined2 *)(iVar1 + -0x806) = 4;
  *(undefined2 *)(iVar1 + -0x808) = 2;
  *(undefined2 *)(iVar1 + -0x80a) = 0;
  *(undefined2 *)(iVar1 + -0x80c) = unaff_CS;
  *(undefined2 *)(iVar1 + -0x80e) = 0x86a;
  MESSAGEBEEP();
  *(undefined2 *)(iVar1 + -0x80c) = 0;
  *(undefined4 *)(iVar1 + -0x810) = param_1;
  *(undefined2 *)(iVar1 + -0x812) = 0x1010;
  *(undefined2 *)(iVar1 + -0x814) = 0x875;
  FATALAPPEXIT();
  return;
}



/* 1010:0000: SNDPLAYSOUND; Thunk to MMSYSTEM::SNDPLAYSOUND */

/* 1010:0004: MIDIOUTOPEN; Thunk to MMSYSTEM::MIDIOUTOPEN */

/* 1010:0008: MIDIOUTCLOSE; Thunk to MMSYSTEM::MIDIOUTCLOSE */

/* 1010:000c: MIDIOUTSHORTMSG; Thunk to MMSYSTEM::MIDIOUTSHORTMSG */

/* 1010:0010: MIDIOUTRESET; Thunk to MMSYSTEM::MIDIOUTRESET */

/* 1010:0014: MIDIOUTGETVOLUME; Thunk to MMSYSTEM::MIDIOUTGETVOLUME */

/* 1010:0018: MIDIOUTSETVOLUME; Thunk to MMSYSTEM::MIDIOUTSETVOLUME */

/* 1010:001c: MIDIOUTGETID; Thunk to MMSYSTEM::MIDIOUTGETID */

/* 1010:0020: WAVEOUTOPEN; Thunk to MMSYSTEM::WAVEOUTOPEN */

/* 1010:0024: WAVEOUTCLOSE; Thunk to MMSYSTEM::WAVEOUTCLOSE */

/* 1010:0028: WAVEOUTPREPAREHEADER; Thunk to MMSYSTEM::WAVEOUTPREPAREHEADER */

/* 1010:002c: WAVEOUTUNPREPAREHEADER; Thunk to MMSYSTEM::WAVEOUTUNPREPAREHEADER */

/* 1010:0030: WAVEOUTWRITE; Thunk to MMSYSTEM::WAVEOUTWRITE */

/* 1010:0034: WAVEOUTPAUSE; Thunk to MMSYSTEM::WAVEOUTPAUSE */

/* 1010:0038: WAVEOUTRESTART; Thunk to MMSYSTEM::WAVEOUTRESTART */

/* 1010:003c: WAVEOUTRESET; Thunk to MMSYSTEM::WAVEOUTRESET */

/* 1010:0040: WAVEOUTGETVOLUME; Thunk to MMSYSTEM::WAVEOUTGETVOLUME */

/* 1010:0044: WAVEOUTSETVOLUME; Thunk to MMSYSTEM::WAVEOUTSETVOLUME */

/* 1010:0048: WAVEOUTGETID; Thunk to MMSYSTEM::WAVEOUTGETID */

/* 1010:004c: TIMESETEVENT; Thunk to MMSYSTEM::TIMESETEVENT */

/* 1010:0050: TIMEKILLEVENT; Thunk to MMSYSTEM::TIMEKILLEVENT */

/* 1010:0054: TIMEGETDEVCAPS; Thunk to MMSYSTEM::TIMEGETDEVCAPS */

/* 1010:0058: TIMEBEGINPERIOD; Thunk to MMSYSTEM::TIMEBEGINPERIOD */

/* 1010:005c: TIMEENDPERIOD; Thunk to MMSYSTEM::TIMEENDPERIOD */

/* 1010:0060: TIMEGETTIME; Thunk to MMSYSTEM::TIMEGETTIME */

/* 1010:0064: GETVERSION; Thunk to KERNEL::GETVERSION */

/* 1010:0068: LOCALINIT; Thunk to KERNEL::LOCALINIT */

/* 1010:006c: DOS3CALL; Thunk to KERNEL::DOS3CALL */

/* 1010:0070: FATALAPPEXIT; Thunk to KERNEL::FATALAPPEXIT */

/* 1010:0074: MESSAGEBEEP; Thunk to USER::MESSAGEBEEP */

