/* Automated Ghidra 11.3.2 C pseudocode. NOT original source or buildable C.
 * Input: PILOTS.EXE
 * Addresses and inferred types require manual validation.
 */

/* Address: 00402010 */

void FUN_00402010(void)

{
  return;
}



/* Address: 00402014 */

void FUN_00402014(int param_1)

{
  char cVar1;
  
  cVar1 = *(char *)(param_1 + 3);
  *(undefined1 *)(param_1 + 3) = *(undefined1 *)(param_1 + 2);
  *(char *)(param_1 + 2) = -cVar1;
  *(byte *)(param_1 + 1) =
       *(byte *)(param_1 + 1) & 0xf | (&DAT_00410084)[(int)(uint)*(byte *)(param_1 + 1) >> 4];
  return;
}



/* Address: 00402044 */

void FUN_00402044(int param_1)

{
  *(char *)(param_1 + 3) = -*(char *)(param_1 + 3);
  *(char *)(param_1 + 2) = -*(char *)(param_1 + 2);
  *(byte *)(param_1 + 1) =
       *(byte *)(param_1 + 1) & 0xf | (&DAT_00410094)[(int)(uint)*(byte *)(param_1 + 1) >> 4];
  return;
}



/* Address: 0040206c */

void FUN_0040206c(int param_1)

{
  char cVar1;
  
  cVar1 = *(char *)(param_1 + 2);
  *(undefined1 *)(param_1 + 2) = *(undefined1 *)(param_1 + 3);
  *(char *)(param_1 + 3) = -cVar1;
  *(byte *)(param_1 + 1) =
       *(byte *)(param_1 + 1) & 0xf | (&DAT_004100a4)[(int)(uint)*(byte *)(param_1 + 1) >> 4];
  return;
}



/* Address: 0040209c */

void FUN_0040209c(int param_1)

{
  *(char *)(param_1 + 2) = -*(char *)(param_1 + 2);
  *(byte *)(param_1 + 1) =
       *(byte *)(param_1 + 1) & 0xf | (&DAT_004100b4)[(int)(uint)*(byte *)(param_1 + 1) >> 4];
  return;
}



/* Address: 004020c0 */

void FUN_004020c0(int param_1)

{
  char cVar1;
  
  cVar1 = *(char *)(param_1 + 2);
  *(char *)(param_1 + 2) = -*(char *)(param_1 + 3);
  *(char *)(param_1 + 3) = -cVar1;
  *(byte *)(param_1 + 1) =
       *(byte *)(param_1 + 1) & 0xf | (&DAT_004100c4)[(int)(uint)*(byte *)(param_1 + 1) >> 4];
  return;
}



/* Address: 004020f0 */

void FUN_004020f0(int param_1)

{
  *(char *)(param_1 + 3) = -*(char *)(param_1 + 3);
  *(byte *)(param_1 + 1) =
       *(byte *)(param_1 + 1) & 0xf | (&DAT_004100d4)[(int)(uint)*(byte *)(param_1 + 1) >> 4];
  return;
}



/* Address: 00402114 */

void FUN_00402114(int param_1)

{
  undefined1 uVar1;
  
  uVar1 = *(undefined1 *)(param_1 + 2);
  *(undefined1 *)(param_1 + 2) = *(undefined1 *)(param_1 + 3);
  *(undefined1 *)(param_1 + 3) = uVar1;
  *(byte *)(param_1 + 1) =
       *(byte *)(param_1 + 1) & 0xf | (&DAT_004100e4)[(int)(uint)*(byte *)(param_1 + 1) >> 4];
  return;
}



/* Address: 004023bc */

void FUN_004023bc(byte *param_1)

{
  if ((*param_1 & 0x80) != 0) {
    FUN_00408b04(param_1);
  }
  FUN_004047a4(param_1);
  return;
}



/* Address: 004023d8 */

void FUN_004023d8(int param_1)

{
  int iVar1;
  byte *pbVar2;
  
  if ((*(byte *)(param_1 + 3) & 2) != 0) {
    *(byte *)(param_1 + 3) = *(byte *)(param_1 + 3) & 0xfd;
    iVar1 = (int)DAT_004161c0 - param_1;
    pbVar2 = DAT_004161c0;
    if (DAT_004161c4 < DAT_004161c0) {
      do {
        if (((*pbVar2 & 0xe3) == 0xe1) && ((uint)*(ushort *)(pbVar2 + 6) == iVar1 / 0x10)) {
          FUN_004023bc(pbVar2);
        }
        pbVar2 = pbVar2 + -0x10;
      } while (DAT_004161c4 < pbVar2);
    }
  }
  return;
}



/* Address: 00402438 */

void FUN_00402438(int param_1)

{
  if (param_1 != 0xffff) {
    FUN_004023bc((byte *)(DAT_004161c0 + param_1 * -0x10));
  }
  return;
}



/* Address: 00402458 */

void FUN_00402458(undefined1 param_1,byte param_2,byte param_3,undefined4 param_4,undefined4 param_5
                 ,undefined4 param_6,undefined4 param_7,byte *param_8)

{
  undefined4 uVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined4 uVar6;
  undefined4 uVar7;
  undefined4 uVar8;
  undefined1 uVar9;
  
  uVar9 = DAT_0041675e;
  uVar8 = DAT_00416350;
  uVar7 = DAT_004161ac;
  uVar6 = DAT_004161a8;
  uVar5 = DAT_004161a4;
  uVar4 = DAT_004161a0;
  uVar3 = DAT_0041619c;
  uVar2 = DAT_00416198;
  uVar1 = DAT_00416194;
  if (param_8 != (byte *)0x0) {
    DAT_00416194 = param_7;
    DAT_00416198 = param_6;
    DAT_0041619c = param_5;
    DAT_00416350 = param_4;
    DAT_004161a0 = (uint)param_3;
    DAT_004161a4 = (uint)param_2;
    DAT_0041675e = param_1;
    DAT_004161a8 = DAT_004161a0;
    DAT_004161ac = DAT_004161a4;
    FUN_00408e64(param_8);
  }
  DAT_00416194 = uVar1;
  DAT_00416198 = uVar2;
  DAT_0041619c = uVar3;
  DAT_004161a0 = uVar4;
  DAT_004161a4 = uVar5;
  DAT_004161a8 = uVar6;
  DAT_004161ac = uVar7;
  DAT_00416350 = uVar8;
  DAT_0041675e = uVar9;
  return;
}



/* Address: 0040255c */

void FUN_0040255c(byte *param_1)

{
  byte *pbVar1;
  int iVar2;
  
  if (param_1 == DAT_00411170) {
    DAT_00411170 = (byte *)0x0;
    iVar2 = 0;
    pbVar1 = DAT_004161c0;
    if (DAT_004161c4 < DAT_004161c0) {
      do {
        if (pbVar1 < param_1) {
          iVar2 = iVar2 + 1;
        }
        if (((*pbVar1 & 3) == 3) && (*(char *)((uint)pbVar1[1] * 0x10 + DAT_004161c8) == '\x03')) {
          if (iVar2 != 0) {
            DAT_00411170 = pbVar1;
            return;
          }
          if (DAT_00411170 == (byte *)0x0) {
            DAT_00411170 = pbVar1;
          }
        }
        pbVar1 = pbVar1 + -0x10;
      } while (DAT_004161c4 < pbVar1);
    }
  }
  return;
}



/* Address: 004025d0 */

void FUN_004025d0(void)

{
  byte *pbVar1;
  int iVar2;
  
  if (*(byte *)(DAT_00416198 + 2) != 0xfe) {
    iVar2 = (uint)*(byte *)(DAT_00416198 + 2) * 0x10 + DAT_004161c8;
    pbVar1 = DAT_004161c0;
    do {
      if ((((*pbVar1 & 0xc1) == 0x81) && (*(short *)(pbVar1 + 6) == -1)) &&
         (*(short *)(pbVar1 + 4) == *(short *)(DAT_00416198 + 6))) {
        FUN_004023bc(pbVar1);
        break;
      }
      pbVar1 = pbVar1 + -0x10;
    } while (DAT_004161c4 < pbVar1);
    FUN_00402458((char)*(undefined4 *)(DAT_00416198 + 3),(byte)*(undefined4 *)(DAT_00416198 + 7),
                 (byte)*(undefined4 *)(DAT_00416198 + 6),0,0xffff,0,iVar2,*(byte **)(iVar2 + 0xc));
    *(undefined2 *)(DAT_00416198 + 2) = 0xf0fe;
  }
  return;
}



/* Address: 00402654 */

void FUN_00402654(char param_1,int param_2,int param_3)

{
  ushort *puVar1;
  int iVar2;
  byte *pbVar3;
  byte *pbVar4;
  char local_c;
  undefined2 local_4;
  
  puVar1 = (ushort *)(DAT_004161d0 + ((param_2 << ((byte)DAT_004161d4 & 0x1f)) + param_3) * 2);
  if ((byte)*puVar1 == 0xfe) {
    return;
  }
  iVar2 = (uint)(byte)*puVar1 * 0x10 + DAT_004161c8;
  pbVar4 = (byte *)0x0;
  local_c = '\0';
  local_4 = CONCAT11((byte)param_2,(byte)param_3);
  pbVar3 = DAT_004161c0;
  do {
    if ((*pbVar3 & 1) != 0) {
      if ((*pbVar3 & 2) == 0) {
        if ((((param_1 != '\0') && (*(short *)(pbVar3 + 4) == local_4)) &&
            (*(short *)(pbVar3 + 6) == -1)) && ((*pbVar3 & 0x40) == 0)) {
          FUN_004023bc(pbVar3);
          if (local_c == '\0') goto LAB_00402726;
          goto LAB_00402735;
        }
      }
      else if (*(short *)(pbVar3 + 6) == local_4) {
        FUN_00402438((uint)*(ushort *)(pbVar3 + 10));
        if (*(ushort *)(pbVar3 + 10) != *(ushort *)(pbVar3 + 8)) {
          FUN_00402438((uint)*(ushort *)(pbVar3 + 8));
        }
        if ((*pbVar3 & 8) != 0) {
          FUN_0040461c(*(int *)(pbVar3 + 0xc));
        }
        FUN_004023d8((int)pbVar3);
        FUN_004047a4(pbVar3);
        FUN_0040255c(pbVar3);
        pbVar4 = pbVar3;
        if ((param_1 == '\0') || (local_c != '\0')) goto LAB_00402735;
LAB_00402726:
        local_c = local_c + '\x01';
      }
    }
    pbVar3 = pbVar3 + -0x10;
    if (pbVar3 <= DAT_004161c4) {
LAB_00402735:
      if (pbVar4 != (byte *)0x0) {
        *puVar1 = *(ushort *)(pbVar4 + 2) & 0xf0ff;
      }
      FUN_00402458((char)*(undefined4 *)((int)puVar1 + 1),(byte)param_2,(byte)param_3,0,0xffff,0,
                   iVar2,*(byte **)(iVar2 + 0xc));
      return;
    }
  } while( true );
}



/* Address: 0040279c */

void FUN_0040279c(char param_1,uint param_2)

{
  byte *pbVar1;
  int iVar2;
  
  FUN_004025d0();
  if (param_1 != '\0') {
    pbVar1 = *(byte **)(param_2 * 0x10 + 4 + DAT_004161cc);
    param_2 = (uint)pbVar1[((DAT_004101c8 >> 0x10) * (uint)*pbVar1 >> 0x10) + 1];
    DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
  }
  *(undefined1 *)(DAT_00416198 + 2) = (undefined1)param_2;
  *(undefined1 *)(DAT_00416198 + 3) = 0x10;
  iVar2 = param_2 * 0x10 + DAT_004161c8;
  FUN_00402458(0x10,(byte)*(undefined4 *)(DAT_00416198 + 7),(byte)*(undefined4 *)(DAT_00416198 + 6),
               0,0xffff,0,iVar2,*(byte **)(iVar2 + 4));
  return;
}



/* Address: 0040283c */

void FUN_0040283c(int param_1,int param_2,uint param_3)

{
  undefined2 *puVar1;
  int iVar2;
  uint uVar3;
  byte unaff_BL;
  uint uVar4;
  byte *pbVar5;
  char *pcVar6;
  uint *puVar7;
  int unaff_retaddr;
  int iVar8;
  
  puVar1 = (undefined2 *)(DAT_004161d0 + ((param_1 << ((byte)DAT_004161d4 & 0x1f)) + param_2) * 2);
  if ((param_3 & 0x100) == 0) {
    if ((param_3 & 0xff) == 0xfe) {
      FUN_00402654('\x01',param_1,param_2);
      *puVar1 = CONCAT11(param_3._1_1_,(undefined1)param_3);
      return;
    }
  }
  else {
    iVar2 = (param_3 & 0xff) * 0x10 + DAT_004161cc;
    iVar8 = *(int *)(iVar2 + 8);
    uVar3 = DAT_004101c8 >> 0x10;
    pbVar5 = *(byte **)(iVar2 + 4);
    DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
    if (iVar8 != 0) {
      uVar4 = (uint)param_3._1_1_;
      param_3._1_1_ = *(byte *)(iVar8 + (uint)pbVar5[(uVar3 * *pbVar5 >> 0x10) + 1]);
      (*(code *)(&PTR_LAB_00410114)[(int)uVar4 >> 4])(&param_3);
    }
  }
  (*(code *)(&PTR_LAB_00410178)[(int)(uint)param_3._1_1_ >> 4])(&param_3);
  DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
  pcVar6 = (char *)((uint)unaff_BL * 0x10 + DAT_004161c8);
  if (*pcVar6 == '\0') {
    FUN_00402654('\x01',unaff_retaddr,param_1);
    pbVar5 = (byte *)0x0;
    iVar8 = 0xffff;
    puVar7 = (uint *)0x0;
  }
  else {
    FUN_00402654('\0',unaff_retaddr,param_1);
    pbVar5 = FUN_00404760();
    *pbVar5 = param_2._1_1_ & 0xf0 | 3;
    pbVar5[1] = unaff_BL;
    pbVar5[4] = 0;
    pbVar5[5] = pcVar6[2];
    pbVar5[6] = (byte)param_1;
    pbVar5[7] = (byte)unaff_retaddr;
    pbVar5[10] = 0xff;
    pbVar5[0xb] = 0xff;
    pbVar5[8] = 0xff;
    pbVar5[9] = 0xff;
    *(undefined2 *)(pbVar5 + 2) = *puVar1;
    if (pbVar5 < DAT_00416198) {
      pbVar5[3] = pbVar5[3] | 1;
    }
    uVar3 = (byte)pcVar6[3] + 1;
    if (uVar3 < 5) {
      pbVar5[0xc] = 0;
      pbVar5[0xd] = 0;
      pbVar5[0xe] = 0;
      pbVar5[0xf] = 0;
      puVar7 = (uint *)(pbVar5 + 0xc);
    }
    else {
      puVar7 = FUN_00404594(uVar3);
      *(uint **)(pbVar5 + 0xc) = puVar7;
      FUN_00408e4c(uVar3,(undefined1 *)puVar7);
      *pbVar5 = *pbVar5 | 8;
    }
    iVar8 = (DAT_004161c0 - (int)pbVar5) / 0x10;
    if ((*pcVar6 == '\x03') && (DAT_00411170 == (byte *)0x0)) {
      DAT_00411170 = pbVar5;
    }
  }
  *puVar1 = CONCAT11(param_2._1_1_,unaff_BL);
  FUN_00402458(param_2._1_1_,(byte)unaff_retaddr,(byte)param_1,puVar7,iVar8,pbVar5,pcVar6,
               *(byte **)(pcVar6 + 4));
  return;
}



/* Address: 00402a68 */

void FUN_00402a68(undefined4 param_1)

{
  ushort *puVar1;
  byte bVar2;
  
  if (param_1._2_1_ != '\0' || param_1._3_1_ != '\0') {
    DAT_004161a0 = (param_1._2_1_ + DAT_004161a0 + DAT_00416168) % DAT_00416168;
    DAT_004161a4 = (param_1._3_1_ + DAT_004161a4 + DAT_0041616c) % DAT_0041616c;
    bVar2 = DAT_00416198[3];
    *(ushort *)(DAT_004161d0 + ((DAT_004161ac << ((byte)DAT_004161d4 & 0x1f)) + DAT_004161a8) * 2) =
         *(ushort *)(DAT_00416198 + 2) & 0xf0ff;
    FUN_00402654('\0',DAT_004161a4,DAT_004161a0);
    DAT_00416198[6] = (byte)DAT_004161a0;
    DAT_00416198[7] = (byte)DAT_004161a4;
    puVar1 = (ushort *)
             (DAT_004161d0 + ((DAT_004161a4 << ((byte)DAT_004161d4 & 0x1f)) + DAT_004161a0) * 2);
    *(ushort *)(DAT_00416198 + 2) = *puVar1;
    DAT_00416198[3] = DAT_00416198[3] | bVar2 & 0xf;
    *puVar1 = (ushort)((*DAT_00416198 & 0xf0) << 8) | (ushort)DAT_00416198[1];
    DAT_004111b4 = 1;
  }
  if ((param_1._1_1_ & 0xf0) != DAT_0041675e) {
    DAT_0041675e = param_1._1_1_ & 0xf0;
    *DAT_00416198 = *DAT_00416198 & 0xf;
    *DAT_00416198 = *DAT_00416198 | DAT_0041675e;
    *(byte *)(DAT_004161d0 + 1 + ((DAT_004161a4 << ((byte)DAT_004161d4 & 0x1f)) + DAT_004161a0) * 2)
         = DAT_0041675e;
    DAT_004111b4 = 1;
  }
  return;
}



/* Address: 00402bc4 */

undefined4 FUN_00402bc4(void)

{
  byte bVar1;
  char *pcVar2;
  undefined4 in_EAX;
  undefined3 uVar4;
  undefined4 uVar3;
  
  pcVar2 = DAT_004162e0;
  uVar4 = (undefined3)((uint)in_EAX >> 8);
  if (DAT_00416768 == '\x0e') {
    uVar4 = 0;
    DAT_004162e0[2] = '\0';
    pcVar2[3] = '\0';
  }
  else {
    DAT_004162e0[3] = DAT_00416768;
    pcVar2[2] = '\x01';
  }
  if (*pcVar2 == '\0') {
    *(int *)(pcVar2 + 4) = *(int *)(pcVar2 + 4) + 1;
    if (pcVar2[1] == '\0') {
      pcVar2[1] = '\x01';
    }
    if (0x32 < (byte)pcVar2[1]) {
      pcVar2[1] = '2';
    }
    bVar1 = pcVar2[1];
    uVar3 = CONCAT31(uVar4,bVar1);
    if (bVar1 != DAT_00416213) {
      DAT_00416213 = bVar1;
      uVar3 = FUN_0040b754(bVar1);
    }
    return CONCAT31((int3)((uint)uVar3 >> 8),1);
  }
  return 0;
}



/* Address: 00402c2c */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00402c2c(byte param_1,char param_2,int param_3,int param_4,int param_5,int param_6)

{
  char cVar1;
  undefined4 uVar2;
  byte *pbVar3;
  int iVar4;
  byte *pbVar5;
  char *pcVar6;
  char local_c;
  byte local_b;
  byte local_a;
  int local_8;
  
  pbVar5 = (byte *)0x0;
  local_b = 0;
  local_a = 0xff;
  local_8 = param_3;
  if (param_2 == -0x71) {
    FUN_0040255c(DAT_00411170);
  }
  else if (param_2 == -0x7e) {
    local_b = 0x90;
  }
  else if (param_2 == -0x7d) {
    local_b = 0xa0;
  }
  else if (((param_2 != -0x7c) && (param_2 != -0x7f)) && (param_2 == '\x0e')) {
    local_8 = param_5;
    param_4 = param_6;
  }
  if (param_1 != 0xe) {
    local_b = local_b | param_1 | 0x40;
  }
  iVar4 = param_4 + _DAT_00411164;
  local_8 = local_8 + DAT_00411168;
  if (DAT_004161c4 < DAT_004161c0) {
    pbVar3 = DAT_004161c0;
    do {
      if ((*pbVar3 & 3) == 3) {
        pcVar6 = (char *)((uint)pbVar3[1] * 0x10 + DAT_004161c8);
        cVar1 = *pcVar6;
        if (cVar1 == '\x01') {
          pbVar3[4] = local_b;
        }
        else if (cVar1 == '\x02') {
LAB_00402d28:
          pbVar3[4] = local_b & 0x4f;
        }
        else if (cVar1 == '\x03') {
          if (pbVar3 == DAT_00411170) goto LAB_00402d28;
          pbVar3[4] = 0;
        }
        if (((pbVar5 == (byte *)0x0) || (pbVar3[5] <= local_a)) &&
           (uVar2 = FUN_00408de8(local_8,iVar4,(int)pbVar3), (char)uVar2 != '\0')) {
          local_c = *pcVar6;
          local_a = pbVar3[5];
          pbVar5 = pbVar3;
        }
      }
      pbVar3 = pbVar3 + -0x10;
    } while (DAT_004161c4 < pbVar3);
  }
  if (pbVar5 == (byte *)0x0) {
    DAT_0041635c = iVar4 / DAT_00416204;
    DAT_00416358 = local_8 / DAT_00416208;
    DAT_0041676b = *(char *)(DAT_004161d0 +
                            ((DAT_00416358 << ((byte)DAT_004161d4 & 0x1f)) + DAT_0041635c) * 2);
    goto LAB_00402e32;
  }
  DAT_0041676b = local_c;
  if (local_c == '\x02') {
    pbVar3 = DAT_00411170;
    if ((param_2 != -0x7e) && (param_2 != -0x7d)) {
      if (param_2 == '\x0e') {
        local_b = local_b | 0x10;
      }
      else if (param_2 == -0x7f) {
        local_b = local_b | 0x20;
      }
    }
LAB_00402ddb:
    DAT_00411170 = pbVar3;
    pbVar5[4] = local_b;
  }
  else if ((local_c == '\x03') && ((pbVar3 = pbVar5, local_b == 0x90 || (local_b == 0xa0))))
  goto LAB_00402ddb;
  DAT_0041635c = (uint)pbVar5[6];
  DAT_00416358 = (uint)pbVar5[7];
LAB_00402e32:
  DAT_0041676d = param_2;
  if ((param_2 == -0x7e) || (param_2 == -0x7d)) {
    DAT_00416766 = param_2;
    DAT_00416765 = DAT_0041676b;
    DAT_00416364 = DAT_0041635c;
    DAT_00416360 = DAT_00416358;
  }
  else {
    if (param_2 == -0x7f) {
      DAT_00416766 = '\x0e';
    }
    DAT_0041676d = '\x0e';
  }
  return;
}



/* Address: 00402e98 */

int FUN_00402e98(byte param_1,byte *param_2)

{
  int iVar1;
  int local_4;
  
  iVar1 = (uint)param_1 - (uint)*param_2;
  if (iVar1 == 0) {
    return 0;
  }
  local_4 = (int)(0xff / (ulonglong)(longlong)(int)(uint)DAT_00416213);
  if (iVar1 < 0) {
    local_4 = -local_4;
    if (iVar1 <= local_4) goto LAB_00402ede;
  }
  else if (local_4 <= iVar1) goto LAB_00402ede;
  local_4 = iVar1;
LAB_00402ede:
  *param_2 = *param_2 + (char)local_4;
  return iVar1;
}



/* Address: 00402ef4 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00402ef4(void)

{
  int iVar1;
  
  iVar1 = FUN_00402e98((byte)_DAT_00416762,(byte *)((int)&DAT_004162b8 + 1));
  if (iVar1 != 0) {
    FUN_0040a250(ram0x004162b9);
  }
  iVar1 = FUN_00402e98((byte)((uint)_DAT_00416762 >> 8),(byte *)((int)&DAT_004162b8 + 2));
  if (iVar1 != 0) {
    FUN_0040a240(CONCAT13(DAT_004162bc._1_1_,DAT_004162b8_2));
  }
  return;
}



/* Address: 00402f34 */

undefined4 FUN_00402f34(char param_1,char param_2,byte *param_3)

{
  undefined4 uVar1;
  uint uVar2;
  
  uVar2 = (uint)param_3[2];
  if (uVar2 < 2) {
    if ((param_2 != '\0') || ((*param_3 & 4) != 0)) {
      uVar1 = FUN_00408b04(param_3);
LAB_00402fa0:
      if (param_1 != '\0') {
        uVar1 = FUN_004047a4(param_3);
      }
      return CONCAT31((int3)((uint)uVar1 >> 8),1);
    }
  }
  else {
    uVar1 = FUN_00408b04(param_3);
    param_3[1] = param_3[1] + 1;
    if (param_3[1] == uVar2) {
      param_3[1] = 0;
      *(uint *)(param_3 + 0xc) = *(int *)(param_3 + 0xc) - (uVar2 * 8 + -8);
      if ((param_2 != '\0') || ((*param_3 & 4) != 0)) goto LAB_00402fa0;
    }
    else {
      *(int *)(param_3 + 0xc) = *(int *)(param_3 + 0xc) + 8;
    }
    if ((*param_3 & 0x40) == 0) {
      FUN_00408a04(param_3);
    }
    FUN_00408b04(param_3);
  }
  return 0;
}



/* Address: 00402fb4 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_00402fb4(void)

{
  ushort uVar1;
  undefined4 uVar2;
  byte *pbVar3;
  uint uVar4;
  int iVar5;
  byte *local_c;
  uint *local_8;
  
  local_c = ram0x004111b8;
  if (ram0x004111b8 == (byte *)0x0) {
    local_c = DAT_004161c0;
  }
  if (local_c <= DAT_004161c4) {
LAB_004030dd:
    DAT_00416198 = (byte *)0x0;
    DAT_0041619c = 0xffff;
    return CONCAT31((int3)((uint)local_c >> 8),1);
  }
LAB_00402fe1:
  if ((*local_c & 3) == 3) {
    if ((DAT_004162dc != 0) &&
       (((uint)*(byte *)(((int)(uint)local_c[1] >> 3) + DAT_004162dc) & 1 << (local_c[1] & 7)) == 0)
       ) goto LAB_004032d4;
    if ((local_c[3] & 1) == 0) {
      if ((*local_c & 4) == 0) {
        if ((*(ushort *)(local_c + 10) != 0xffff) &&
           (uVar2 = FUN_00402f34('\x01','\0',DAT_004161c0 + (uint)*(ushort *)(local_c + 10) * -0x10)
           , (char)uVar2 != '\0')) {
          uVar1 = *(ushort *)(local_c + 8);
          *(ushort *)(local_c + 10) = uVar1;
          if (uVar1 != 0xffff) {
            pbVar3 = DAT_004161c0 + (uint)uVar1 * -0x10;
            *pbVar3 = *pbVar3 | 0x80;
            pbVar3[4] = local_c[6];
            pbVar3[5] = local_c[7];
            FUN_00408a04(pbVar3);
            FUN_00408b04(pbVar3);
          }
        }
      }
      else {
        uVar2 = FUN_00402f34(*(ushort *)(local_c + 10) != *(ushort *)(local_c + 8),'\x01',
                             DAT_004161c0 + (uint)*(ushort *)(local_c + 10) * -0x10);
        if ((char)uVar2 == '\0') {
          if (local_c != ram0x004111b8) goto LAB_004032d4;
          goto LAB_004030dd;
        }
        uVar1 = *(ushort *)(local_c + 8);
        *(ushort *)(local_c + 10) = uVar1;
        if (uVar1 != 0xffff) {
          pbVar3 = DAT_004161c0 + (uint)uVar1 * -0x10;
          *pbVar3 = *pbVar3 | 0x80;
          pbVar3[4] = local_c[6];
          pbVar3[5] = local_c[7];
          FUN_00408a04(pbVar3);
          FUN_00408b04(pbVar3);
        }
        *local_c = *local_c & 0xfb;
      }
      DAT_00416198 = local_c;
      DAT_0041619c = ((int)DAT_004161c0 - (int)local_c) / 0x10;
      DAT_00416194 = (uint)local_c[1] * 0x10 + DAT_004161c8;
      if ((*local_c & 8) == 0) {
        DAT_00416350 = local_c + 0xc;
      }
      else {
        DAT_00416350 = *(byte **)(local_c + 0xc);
      }
      local_8 = *(uint **)(DAT_00416194 + 8);
      DAT_0041676c = 0;
      if (local_8 != (uint *)0x0) {
        do {
          iVar5 = 0;
          DAT_0041675e = *DAT_00416198 & 0xf0;
          if (DAT_0041675e == 0x80) {
            iVar5 = 1;
LAB_004031f0:
            iVar5 = iVar5 + 1;
LAB_004031f1:
            iVar5 = iVar5 + 1;
          }
          else {
            if (DAT_0041675e == 0x40) goto LAB_004031f0;
            if (DAT_0041675e == 0x20) goto LAB_004031f1;
          }
          uVar4 = 0;
          do {
            if (((uint)*(byte *)(DAT_00416194 + 1) & 1 << ((byte)uVar4 & 0x1f)) != 0) {
              DAT_00416354 = (uVar4 & 3) + iVar5 & 3;
              if (3 < uVar4) {
                DAT_00416354 = DAT_00416354 + 4;
              }
              DAT_004161b0 = (&PTR_FUN_004100f4)[DAT_00416354];
              DAT_00416767 = '\0';
              pbVar3 = (byte *)FUN_00403a64('\0',(uint *)*local_8);
              if (pbVar3 != (byte *)0x0) {
                if (pbVar3 != (byte *)0x1) {
                  if (ram0x004111b8 != (byte *)0x0) goto LAB_00403304;
                  FUN_0040255c(local_c);
                  goto LAB_004032d4;
                }
                if (DAT_00416767 == '\0') goto LAB_004032fb;
                uVar4 = 0;
                break;
              }
            }
            uVar4 = uVar4 + 1;
          } while ((int)uVar4 < 8);
          pbVar3 = (byte *)*local_8;
          if ((*pbVar3 & 0x80) != 0) goto code_r0x0040328d;
          local_8 = local_8 + 1;
        } while( true );
      }
    }
    else {
      local_c[3] = local_c[3] & 0xfe;
    }
  }
  else if (((DAT_004162dc == 0) && ((*local_c & 0x83) == 0x81)) && (*(short *)(local_c + 6) == -1))
  {
    FUN_00402f34('\x01','\0',local_c);
  }
LAB_004032d4:
  local_c = local_c + -0x10;
  if (local_c <= DAT_004161c4) goto LAB_004030dd;
  goto LAB_00402fe1;
code_r0x0040328d:
  if (uVar4 == 0) {
    unique0x00009680 = local_c;
    goto LAB_004030dd;
  }
LAB_004032fb:
  if (ram0x004111b8 != (byte *)0x0) {
LAB_00403304:
    local_c = pbVar3;
    ram0x004111b8 = (byte *)0x0;
    goto LAB_004030dd;
  }
  goto LAB_004032d4;
}



/* Address: 00403314 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_00403314(char param_1)

{
  byte *pbVar1;
  uint uVar2;
  undefined4 uVar3;
  byte *pbVar4;
  
  pbVar4 = DAT_004161c0;
  if (DAT_004161c4 < DAT_004161c0) {
    do {
      pbVar1 = DAT_004161c0;
      if ((*pbVar4 & 0x87) == 0x81) {
        if ((pbVar4[1] + 1 == (uint)pbVar4[2]) && (*(short *)(pbVar4 + 6) != -1)) {
          pbVar4[4] = DAT_004161c0[(uint)*(ushort *)(pbVar4 + 6) * -0x10 + 6];
          pbVar4[5] = pbVar1[(uint)*(ushort *)(pbVar4 + 6) * -0x10 + 7];
        }
        FUN_00402f34('\0','\0',pbVar4);
      }
      pbVar4 = pbVar4 + -0x10;
    } while (DAT_004161c4 < pbVar4);
  }
  uVar2 = _DAT_00416768 & 0xff;
  if (((uVar2 != 0x20) && (uVar2 != 0xd)) && ((uVar2 == 0xe || (param_1 != '\x0e')))) {
    return 1;
  }
  uVar3 = FUN_00407058((byte)_DAT_00416768);
  return uVar3;
}



/* Address: 004033a8 */

undefined4 FUN_004033a8(int param_1,int param_2)

{
  undefined4 in_EAX;
  uint uVar1;
  uint uVar2;
  
  if (DAT_00416215 != '\0') goto LAB_00403421;
  uVar2 = (uint)DAT_00410076;
  if (uVar2 < 2) {
    if ((DAT_00410074 & 4) == 0) {
      if (((DAT_0041007c == param_2) && (DAT_0041007e == param_1)) && (DAT_004111b5._1_1_ != '\0'))
      {
        return CONCAT31((int3)(char)((ushort)DAT_0041007e >> 8),1);
      }
      uVar1 = FUN_00408b04(&DAT_00410074);
    }
    else {
LAB_0040343e:
      uVar1 = FUN_00403484('\x01',0);
    }
  }
  else {
    FUN_00408b04(&DAT_00410074);
    DAT_00410075 = DAT_00410075 + 1;
    uVar1 = (uint)DAT_00410075;
    if (uVar1 == uVar2) {
      if ((DAT_00410074 & 4) != 0) goto LAB_0040343e;
      DAT_00410075 = 0;
      uVar1 = uVar2 * 8 - 8;
      DAT_00410080 = DAT_00410080 - uVar1;
    }
    else {
      DAT_00410080 = DAT_00410080 + 8;
    }
  }
  DAT_0041007c = (short)param_2;
  in_EAX = CONCAT22((short)(uVar1 >> 0x10),(short)param_1);
  DAT_0041007e = (short)param_1;
  if (DAT_004111b5._1_1_ != '\0') {
    in_EAX = FUN_00408b04(&DAT_00410074);
  }
LAB_00403421:
  DAT_00416770 = 1;
  return CONCAT31((int3)((uint)in_EAX >> 8),1);
}



/* Address: 00403484 */

undefined4 FUN_00403484(char param_1,int param_2)

{
  int iVar1;
  int in_EAX;
  
  if (DAT_00416215 == '\0') {
    if ((param_1 != '\0') && (DAT_00410080 != 0)) {
      FUN_00408b04(&DAT_00410074);
    }
    DAT_004162c4 = param_2;
    iVar1 = DAT_004161dc + param_2 * 8;
    DAT_00410074 = 0xc1;
    DAT_00410076 = *(undefined1 *)(iVar1 + 3);
    DAT_00410075 = 0;
    DAT_0041007e = 0;
    DAT_0041007c = 0;
    if (*(char *)(iVar1 + 2) == '\x01') {
      in_EAX = *(int *)(iVar1 + 4);
      DAT_00410080 = in_EAX;
    }
    else {
      in_EAX = **(int **)(iVar1 + 4);
      DAT_00410080 = in_EAX;
    }
  }
  return CONCAT31((int3)((uint)in_EAX >> 8),1);
}



/* Address: 004034fc */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_004034fc(void)

{
  int iVar1;
  undefined4 *puVar2;
  ushort *puVar3;
  int *piVar4;
  uint uVar5;
  byte *pbVar6;
  uint uVar7;
  code *pcVar8;
  int iVar9;
  int iVar10;
  int *piVar11;
  uint uVar12;
  int *local_1050;
  int local_104c;
  int local_1048;
  int local_1044 [1024];
  int *local_44;
  int local_40;
  int local_3c;
  int local_38;
  int iVar13;
  int *piVar14;
  
  FUN_0040d333();
  local_1050 = DAT_0041116c;
  piVar4 = DAT_0041116c;
  if (DAT_0041116c != (int *)0x0) {
    local_1048 = 0;
    iVar10 = local_1048;
    for (pbVar6 = DAT_004161c0; local_1048 = iVar10, DAT_004161c4 < pbVar6; pbVar6 = pbVar6 + -0x10)
    {
      if ((*pbVar6 & 0x83) == 0x81) {
        local_1048 = iVar10 + 1;
        local_1044[iVar10] = (int)pbVar6;
      }
      iVar10 = local_1048;
    }
    if (iVar10 != 0) {
      piVar4 = local_1044;
      iVar13 = 1;
      piVar14 = local_1044;
      do {
        piVar14 = piVar14 + 1;
        piVar11 = piVar14;
        for (iVar9 = iVar13; iVar9 < iVar10; iVar9 = iVar9 + 1) {
          iVar1 = *piVar4;
          if (*(byte *)(iVar1 + 3) < *(byte *)(*piVar11 + 3)) {
            *piVar4 = *piVar11;
            *piVar11 = iVar1;
          }
          piVar11 = piVar11 + 1;
        }
        piVar4 = piVar4 + 1;
        iVar13 = iVar13 + 1;
      } while (iVar13 < iVar10 + 1);
    }
    if ((DAT_00416215 == '\0') && (DAT_004111b5._1_1_ != '\0')) {
      local_1048 = iVar10 + 1;
      local_1044[iVar10] = (int)&DAT_00410074;
    }
    do {
      local_44 = (int *)local_1050[4];
      local_40 = *local_1050 - _DAT_00411164;
      if (local_40 < 0) {
        local_40 = 0;
      }
      local_3c = local_1050[2] - _DAT_00411164;
      if (_DAT_0041115c < local_3c) {
        local_3c = _DAT_0041115c;
      }
      if (local_40 < local_3c) {
        local_38 = local_1050[1] - DAT_00411168;
        if (local_38 < 0) {
          local_38 = 0;
        }
        iVar10 = local_38;
        uVar5 = local_1050[3] - DAT_00411168;
        if ((int)DAT_00411160 < local_1050[3] - DAT_00411168) {
          uVar5 = DAT_00411160;
        }
        if (local_38 < (int)uVar5) {
          iVar13 = -local_38;
          local_38 = 0x4036bd;
          FUN_004090c4(uVar5,local_3c,iVar10,local_40,*(undefined4 *)(DAT_004161ec + 0xc),
                       *(int *)(DAT_004161ec + 8),iVar13,-local_40,DAT_004161ec + 0x18);
          local_104c = 0;
          if (0 < local_1048) {
            piVar4 = local_1044;
            do {
              pbVar6 = (byte *)*piVar4;
              puVar2 = *(undefined4 **)(pbVar6 + 0xc);
              puVar3 = (ushort *)*puVar2;
              iVar10 = *(short *)(pbVar6 + 8) - _DAT_00411164;
              iVar13 = *(short *)(pbVar6 + 10) - DAT_00411168;
              if ((*pbVar6 & 0x40) != 0) {
                iVar10 = iVar10 - *(short *)(puVar2 + 1);
                iVar13 = iVar13 - *(short *)((int)puVar2 + 6);
              }
              if ((((iVar10 < local_3c) && (iVar13 < (int)uVar5)) &&
                  (uVar7 = (uint)*puVar3, local_40 < (int)(uVar7 + iVar10))) &&
                 (local_38 < (int)((uint)puVar3[1] + iVar13))) {
                uVar12 = iVar13 - local_38;
                FUN_00405a08(puVar3);
                if (-1 < (int)uVar12) {
                  uVar12 = 0;
                }
                if ((int)uVar7 <= (int)uVar5) {
                  uVar5 = uVar7;
                }
                if ((uVar12 & 8) == 0) {
                  if ((uVar12 & 0x10) == 0) {
                    pcVar8 = FUN_00409174;
                  }
                  else {
                    pcVar8 = FUN_004092e0;
                  }
                }
                else if ((uVar12 & 0x10) == 0) {
                  pcVar8 = FUN_00409368;
                }
                else {
                  pcVar8 = FUN_004093f4;
                }
                local_38 = 0x4037eb;
                (*pcVar8)();
              }
              piVar4 = piVar4 + 1;
              local_104c = local_104c + 1;
            } while (local_104c < local_1048);
          }
          FUN_0040968c((char)DAT_004111b5,uVar5,local_3c,local_38,local_40);
        }
      }
      FUN_0040461c((int)local_1050);
      local_1050 = local_44;
    } while (local_44 != (int *)0x0);
    piVar4 = (int *)FUN_0040bb58();
    DAT_0041116c = (int *)0x0;
    DAT_004111b5 = DAT_004111b5 & 0xffffff00;
  }
  return CONCAT31((int3)((uint)piVar4 >> 8),1);
}



/* Address: 0040385c */

void FUN_0040385c(void)

{
  int iVar1;
  
  iVar1 = FUN_0040c42c((int *)0x0);
  DAT_004101c8 = iVar1 * 0x41c64e6d + 0x3039;
  DAT_004161bc = 0xffff;
  DAT_0041619c = 0xffff;
  DAT_00416198 = 0;
  DAT_00416194 = 0;
  DAT_004161b8 = 0;
  DAT_004162ec = 0;
  DAT_004162e8 = 0;
  DAT_004162e4 = 0;
  DAT_00416769 = 0;
  DAT_0041676a = 0;
  DAT_00416770 = 0;
  DAT_00416761 = 1;
  DAT_0041676f = 0;
  DAT_0041676e = 0;
  DAT_00416763 = 0;
  DAT_00416762 = 0;
  DAT_004111b5._1_1_ = 0;
  FUN_00405514((char *)0x0);
  return;
}



/* Address: 00403900 */

void FUN_00403900(void)

{
  DAT_00416770 = 0;
  return;
}



/* Address: 00403908 */

void FUN_00403908(void)

{
  int iVar1;
  
  iVar1 = FUN_0040c42c((int *)0x0);
  DAT_004101c8 = iVar1 * 0x41c64e6d + 0x3039;
  DAT_00411170 = 0;
  DAT_0041116c = 0;
  return;
}



/* Address: 00403948 */

void FUN_00403948(char param_1)

{
  int iVar1;
  int *piVar2;
  int *piVar3;
  
  piVar2 = DAT_0041116c;
  piVar3 = DAT_0041116c;
  if (DAT_0041116c != (int *)0x0) {
    do {
      piVar3 = (int *)piVar2[4];
      if (param_1 != '\0') {
        FUN_0040968c('\0',piVar2[3],piVar2[2],piVar2[1],*piVar2);
      }
      FUN_0040461c((int)piVar2);
      piVar2 = piVar3;
    } while (piVar3 != (int *)0x0);
  }
  FUN_0040bb58();
  DAT_00416765 = 0xff;
  DAT_00416766 = 0xe;
  DAT_004162dc = 0;
  DAT_0041116c = piVar3;
  iVar1 = FUN_0040c42c((int *)0x0);
  DAT_004101c8 = iVar1 * 0x41c64e6d + 0x3039;
  DAT_00411170 = 0;
  FUN_0040255c((byte *)0x0);
  return;
}



/* Address: 004039d8 */

void FUN_004039d8(void)

{
  FUN_0040c4a0(0x411174,1);
  return;
}



/* Address: 004039e8 */

undefined4 FUN_004039e8(void)

{
  uint unaff_retaddr;
  
  (*DAT_004161b0)(&stack0x00000004);
  FUN_0040283c((int)(char)(unaff_retaddr >> 0x18),(int)(char)(unaff_retaddr >> 0x10),unaff_retaddr);
  return 0;
}



/* Address: 00403a64 */

int FUN_00403a64(char param_1,uint *param_2)

{
  byte bVar1;
  int iVar2;
  int iVar3;
  byte bVar4;
  int *piVar5;
  uint uVar6;
  uint *puVar7;
  uint *puVar8;
  int aiStack_40 [5];
  undefined1 uStack_2c;
  byte bStack_2b;
  undefined4 local_28;
  undefined4 uStack_24;
  uint uStack_20;
  uint *puStack_1c;
  int local_18;
  int iStack_14;
  undefined2 uStack_10;
  byte bStack_e;
  byte bStack_c;
  int *piStack_8;
  int *piStack_4;
  
  local_18 = 0;
  if (param_1 == '\0') {
    DAT_004161a8 = (uint)DAT_00416198[6];
    DAT_004161ac = (uint)DAT_00416198[7];
    DAT_004161b4 = (code *)&LAB_00403a10;
  }
  else {
    DAT_00416198 = (byte *)0x0;
    DAT_0041619c = 0xffff;
    DAT_00416194 = 0;
    DAT_004161b8 = 0;
    DAT_004161bc = 0xffff;
    DAT_004161ac = 0;
    DAT_004161a8 = 0;
    DAT_0041675e = 0x10;
    DAT_004161b0 = FUN_00402010;
    DAT_004161b4 = FUN_004039e8;
    DAT_00416350 = 0;
  }
  local_28 = *param_2;
  puVar7 = param_2 + 1;
  DAT_004161a0 = DAT_004161a8;
  DAT_004161a4 = DAT_004161ac;
  if ((*param_2 & 1) != 0) {
    iVar2 = FUN_00408e64((byte *)*puVar7);
    if (iVar2 == 0) {
      return 0;
    }
    puVar7 = param_2 + 2;
  }
  if ((local_28 & 2) != 0) {
    iStack_14 = -1;
    puVar8 = puVar7;
LAB_00403b3d:
    do {
      uStack_20 = *puVar8;
      puVar7 = puVar8 + 1;
      puStack_1c = puVar7 + (uStack_20 & 0xffff);
      if (uStack_20 >> 0x18 == 4) {
        iVar2 = iStack_14 + 1;
        iStack_14 = iVar2;
        if (iVar2 == 0) {
          aiStack_40[1] = 0;
          aiStack_40[0] = 0;
        }
        else {
          iVar3 = *(int *)(&stack0xffffffbc + iVar2 * 8);
          aiStack_40[iVar2 * 2 + 1] = iVar3;
          aiStack_40[iVar2 * 2] = iVar3;
        }
      }
      else {
        iStack_14 = -1;
      }
      piStack_8 = aiStack_40 + iStack_14 * 2;
      piStack_4 = aiStack_40 + iStack_14 * 2 + 1;
      do {
        uStack_24 = *puVar7;
        puVar7 = puVar7 + 1;
        (*DAT_004161b0)(&uStack_24);
        if (param_1 == '\0') {
          uStack_10 = *(undefined2 *)
                       (DAT_004161d0 +
                       ((int)((int)(char)uStack_24._2_1_ + DAT_004161a0 + DAT_00416168) %
                        DAT_00416168 +
                       ((int)((int)(char)uStack_24._3_1_ + DAT_004161a4 + DAT_0041616c) %
                        DAT_0041616c << ((byte)DAT_004161d4 & 0x1f))) * 2);
        }
        else {
          uStack_10 = *(undefined2 *)
                       (DAT_004161d0 +
                       (((uint)uStack_24._3_1_ << ((byte)DAT_004161d4 & 0x1f)) +
                       (uint)uStack_24._2_1_) * 2);
        }
        uVar6 = (uint)(byte)uStack_10;
        bStack_e = 0;
        bVar1 = uStack_24._1_1_;
        if ((uStack_24 & 0x100) == 0) {
          if ((uStack_24 & 0xff) == uVar6) goto LAB_00403cb3;
        }
        else if ((uVar6 != 0xfe) &&
                (piVar5 = (int *)((uStack_24 & 0xff) * 0x10 + DAT_004161cc),
                ((uint)*(byte *)((uint)((byte)uStack_10 >> 3) + *piVar5) &
                1 << ((byte)uStack_10 & 7)) != 0)) {
          iVar2 = piVar5[2];
          if (iVar2 != 0) {
            bVar4 = uStack_24._1_1_ & 0xf;
            uStack_24._0_2_ = CONCAT11(bVar4 | *(byte *)(iVar2 + uVar6),(undefined1)uStack_24);
            (*(code *)(&PTR_LAB_00410114)[(int)(uint)bVar1 >> 4])(&uStack_24);
          }
LAB_00403cb3:
          if ((uStack_10._1_1_ & uStack_24._1_1_ & 0xf0) != 0) {
            bStack_e = 2;
          }
        }
        if ((uStack_24._1_1_ & 2) == bStack_e) {
          puVar8 = puStack_1c;
          if ((uStack_24 & 0xc00) == 0) goto LAB_00403b3d;
          if ((uStack_24._1_1_ & 0xc) == 4) {
            return 0;
          }
          if ((uStack_24._1_1_ & 0xc) == 8) goto joined_r0x00403d1d;
          iVar2 = *piStack_4;
          *(undefined2 *)(&DAT_004111bc + iVar2 * 2) = uStack_24._2_2_;
          *piStack_4 = iVar2 + 1;
        }
        else if (((puVar7 == puStack_1c) && (-1 < iStack_14)) && (*piStack_8 == *piStack_4)) {
          return 0;
        }
      } while (puVar7 != puStack_1c);
      puVar8 = puVar7;
    } while ((uStack_20 & 0x10000) == 0);
  }
LAB_00403d4b:
  DAT_004111b4 = '\0';
  if ((local_28 & 4) != 0) {
    bVar1 = local_28._1_1_;
    (*DAT_004161b0)(&local_28);
    (*(code *)(&PTR_LAB_00410178)[(int)(uint)bStack_2b >> 4])(&uStack_2c);
    DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
    bVar4 = local_28._1_1_ & 0xf0;
    local_28._0_2_ = CONCAT11(bVar4 | bVar1 & 0xf,(undefined1)local_28);
    FUN_00402a68(local_28);
  }
  DAT_00416764 = '\0';
  if ((local_28 & 8) != 0) {
    if (DAT_00416198 != (byte *)0x0) {
      bStack_c = DAT_00416198[5];
    }
    FUN_00408e64((byte *)*puVar7);
    puVar7 = puVar7 + 1;
    if (((DAT_00416764 == '\0') && (DAT_004111b4 == '\0')) &&
       ((DAT_00416198 != (byte *)0x0 &&
        ((DAT_00416198[5] != bStack_c && (*(short *)(DAT_00416198 + 10) != -1)))))) {
      FUN_00408b48((uint)*(ushort *)(DAT_00416198 + 10));
    }
  }
  if ((DAT_00416764 == '\0') && (DAT_004111b4 != '\0')) {
    FUN_00408d88(DAT_00416198);
  }
  if ((local_28 & 0x10) != 0) {
    iVar2 = -1;
    uStack_20 = *puVar7;
    puVar8 = puVar7 + 1;
    puStack_1c = puVar8 + (*puVar7 & 0xffff);
    do {
      switch(uStack_20 >> 0x18) {
      case 0:
        goto switchD_00403fdd_caseD_0;
      case 1:
        uVar6 = DAT_004101c8 >> 0x10;
        DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
        uVar6 = (uStack_20 & 0xffff) * uVar6 >> 0x10;
        do {
          if (uVar6 != 0) {
            iVar3 = (*DAT_004161b4)(*puVar8);
            local_18 = local_18 + iVar3;
          }
          puVar8 = puVar8 + 1;
          uVar6 = uVar6 - 1;
        } while (puVar8 != puStack_1c);
        break;
      case 2:
        uVar6 = DAT_004101c8 >> 0x10;
        DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
        for (uVar6 = (uStack_20 & 0xffff) * uVar6 >> 0x10; uVar6 != 0; uVar6 = uVar6 - 1) {
          puVar8 = puVar8 + 1;
        }
LAB_004040c4:
        iVar3 = (*DAT_004161b4)(*puVar8);
        local_18 = local_18 + iVar3;
        break;
      case 3:
        do {
          uVar6 = DAT_004101c8 >> 0x10;
          DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
          if (uVar6 * 2 >> 0x10 != 0) {
            iVar3 = (*DAT_004161b4)(*puVar8);
            local_18 = local_18 + iVar3;
          }
          puVar8 = puVar8 + 1;
        } while (puVar8 != puStack_1c);
        break;
      default:
        iVar2 = iVar2 + 1;
        uVar6 = DAT_004101c8 >> 0x10;
        DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
        do {
          if ((*puVar8 & 0xffff0000) ==
              (uint)*(ushort *)
                     (&DAT_004111bc +
                     ((aiStack_40[iVar2 * 2 + 1] - aiStack_40[iVar2 * 2]) * uVar6 +
                      aiStack_40[iVar2 * 2] >> 0x10) * 2) * 0x10000) goto LAB_004040c4;
          puVar8 = puVar8 + 1;
        } while (puVar8 != puStack_1c);
      }
      if ((uStack_20 & 0x10000) != 0) break;
      puVar8 = puStack_1c + 1;
      uStack_20 = *puStack_1c;
      puStack_1c = puVar8 + (*puStack_1c & 0xffff);
    } while( true );
  }
LAB_00403ee7:
  return local_18 + 1;
joined_r0x00403d1d:
  while (puVar7 = puStack_1c, puStack_1c = puVar7, (uStack_20 & 0x10000) == 0) {
    uStack_20 = *puVar7;
    puStack_1c = puVar7 + (uStack_20 & 0xffff) + 1;
  }
  goto LAB_00403d4b;
switchD_00403fdd_caseD_0:
  uVar6 = (DAT_004101c8 >> 0x10) * (local_28 >> 8 & 3) >> 0x10;
  if (uVar6 == 2) {
    puStack_1c = puStack_1c + (*puStack_1c & 0xffff) + 1;
LAB_00403ea0:
    puVar8 = puStack_1c + 1;
    puStack_1c = puVar8 + (*puStack_1c & 0xffff);
  }
  else if (uVar6 == 1) goto LAB_00403ea0;
  DAT_004101c8 = DAT_004101c8 * 0x41c64e6d + 0x3039;
  do {
    iVar2 = (*DAT_004161b4)(*puVar8);
    local_18 = local_18 + iVar2;
    puVar8 = puVar8 + 1;
  } while (puVar8 != puStack_1c);
  goto LAB_00403ee7;
}



/* Address: 004040e4 */

undefined4 FUN_004040e4(uint *param_1)

{
  char cVar1;
  uint *puVar2;
  undefined4 uVar3;
  int iVar4;
  
  puVar2 = FUN_0040c5d8((uint *)&DAT_004162f8,param_1);
  uVar3 = FUN_00404b98((LPCSTR)puVar2);
  if (((((char)uVar3 != '\0') && (cVar1 = FUN_0040385c(), cVar1 != '\0')) &&
      (uVar3 = FUN_0040b3e0(), (char)uVar3 != '\0')) &&
     (((uVar3 = FUN_0040b754((byte)*(undefined4 *)(DAT_004162e0 + 1)), (char)uVar3 != '\0' &&
       (uVar3 = FUN_00409079(), (char)uVar3 != '\0')) &&
      (uVar3 = FUN_0040a0f4(&DAT_0041676f,&DAT_0041676e,0), (char)uVar3 != '\0')))) {
    DAT_00416762 = DAT_0041676e;
    DAT_00416763 = DAT_0041676f;
    iVar4 = FUN_0040691c();
    if ((char)iVar4 != '\0') {
      return 1;
    }
  }
  return 0;
}



/* Address: 00404170 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4
FUN_00404170(undefined1 param_1,undefined1 param_2,byte param_3,char param_4,int param_5,int param_6
            ,int param_7,int param_8,undefined4 param_9,undefined4 param_10)

{
  char cVar1;
  int iVar2;
  undefined4 uVar3;
  int unaff_EBX;
  undefined4 unaff_retaddr;
  
  iVar2 = FUN_0040c500((undefined4 *)&DAT_00411174,0,unaff_EBX,unaff_retaddr);
  if ((iVar2 != 0) || (DAT_004162b6 == '\0')) {
    param_4 = '\x0e';
    param_3 = 0xe;
    param_2 = 0xe;
  }
  _DAT_0041115c = param_10;
  DAT_00411160 = param_9;
  DAT_00416768 = param_2;
  DAT_004111b5._1_1_ = param_1;
  FUN_00402c2c(param_3,param_4,param_5,param_6,param_7,param_8);
  FUN_00402ef4();
  uVar3 = FUN_00402bc4();
  if ((char)uVar3 != '\0') {
    if (DAT_0041676a == '\0') {
      uVar3 = FUN_00402fb4();
      cVar1 = (char)uVar3;
    }
    else {
      uVar3 = FUN_00403314(param_4);
      cVar1 = (char)uVar3;
    }
    while (cVar1 != '\0') {
      while( true ) {
        if (DAT_004162dc == 0) {
          uVar3 = FUN_004033a8(param_7,param_8);
          if (((char)uVar3 == '\0') || (uVar3 = FUN_004034fc(), (char)uVar3 == '\0')) {
            uVar3 = 0;
          }
          else {
            uVar3 = 1;
          }
          return uVar3;
        }
        DAT_00416768 = 0xe;
        uVar3 = FUN_00402bc4();
        if ((char)uVar3 == '\0') {
          return 0;
        }
        if (DAT_0041676a != '\0') break;
        uVar3 = FUN_00402fb4();
        if ((char)uVar3 == '\0') {
          return 0;
        }
      }
      uVar3 = FUN_00403314(param_4);
      cVar1 = (char)uVar3;
    }
  }
  return 0;
}



/* Address: 0040426c */

void FUN_0040426c(uint param_1,uint param_2,uint param_3,uint param_4)

{
  uint uVar1;
  char cVar2;
  uint *puVar3;
  uint *puVar4;
  uint *puVar5;
  
  if (DAT_0041116c == (uint *)0x0) {
    puVar3 = FUN_00404594(0x14);
    DAT_0041116c = puVar3;
LAB_004043ce:
    *puVar3 = param_4;
    puVar3[1] = param_3;
    puVar3[2] = param_2;
    puVar3[3] = param_1;
    puVar3[4] = 0;
    return;
  }
  puVar5 = DAT_0041116c;
  if ((int)param_2 < (int)*DAT_0041116c) goto LAB_00404386;
  if ((int)param_1 < (int)DAT_0041116c[1]) goto LAB_00404386;
  if ((int)DAT_0041116c[2] < (int)param_4) goto LAB_00404386;
  uVar1 = DAT_0041116c[3];
  puVar3 = DAT_0041116c;
  do {
    puVar5 = puVar3;
    if ((int)param_3 <= (int)uVar1) {
      uVar1 = *puVar3;
      if ((int)param_4 < (int)uVar1) {
        *puVar3 = param_4;
      }
      cVar2 = (int)param_4 < (int)uVar1;
      if ((int)puVar3[2] < (int)param_2) {
        puVar3[2] = param_2;
        cVar2 = cVar2 + '\x01';
      }
      if ((int)param_3 < (int)puVar3[1]) {
        puVar3[1] = param_3;
        cVar2 = cVar2 + '\x01';
      }
      if ((int)puVar3[3] < (int)param_1) {
        puVar3[3] = param_1;
        cVar2 = cVar2 + '\x01';
      }
      if (cVar2 != '\0') {
LAB_004042fd:
        puVar3 = DAT_0041116c;
        puVar5 = (uint *)0x0;
        do {
          puVar4 = puVar3;
          puVar3 = (uint *)puVar4[4];
          if (puVar3 != (uint *)0x0) {
            do {
              if ((((int)*puVar3 <= (int)puVar4[2]) && ((int)puVar3[1] <= (int)puVar4[3])) &&
                 (((int)*puVar4 <= (int)puVar3[2] && ((int)puVar4[1] <= (int)puVar3[3])))) {
                if ((int)*puVar4 < (int)*puVar3) {
                  *puVar3 = *puVar4;
                }
                if ((int)puVar3[2] < (int)puVar4[2]) {
                  puVar3[2] = puVar4[2];
                }
                if ((int)puVar4[1] < (int)puVar3[1]) {
                  puVar3[1] = puVar4[1];
                }
                if ((int)puVar3[3] < (int)puVar4[3]) {
                  puVar3[3] = puVar4[3];
                }
                puVar3 = (uint *)puVar4[4];
                if (puVar5 != (uint *)0x0) {
                  puVar5[4] = puVar4[4];
                  puVar3 = DAT_0041116c;
                }
                DAT_0041116c = puVar3;
                FUN_0040461c((int)puVar4);
                goto LAB_004042fd;
              }
              puVar3 = (uint *)puVar3[4];
            } while (puVar3 != (uint *)0x0);
          }
          puVar3 = (uint *)puVar4[4];
          puVar5 = puVar4;
        } while ((uint *)puVar4[4] != (uint *)0x0);
      }
      return;
    }
LAB_00404386:
    do {
      puVar3 = (uint *)puVar5[4];
      if (puVar3 == (uint *)0x0) {
        puVar3 = FUN_00404594(0x14);
        puVar5[4] = (uint)puVar3;
        goto LAB_004043ce;
      }
      puVar5 = puVar3;
    } while ((((int)param_2 < (int)*puVar3) || ((int)param_1 < (int)puVar3[1])) ||
            ((int)puVar3[2] < (int)param_4));
    uVar1 = puVar3[3];
  } while( true );
}



/* Address: 004043f0 */

undefined4 FUN_004043f0(void)

{
  undefined4 uVar1;
  
  DAT_004111b5._0_1_ = DAT_00416216;
  FUN_0040b780('\x01',DAT_00416216,0x100,
               (BYTE *)(*(int *)(DAT_004161ec + 0x10) + DAT_004161ec + 0x18));
  uVar1 = FUN_0040426c(DAT_00416174,DAT_00416170,0,0);
  return CONCAT31((int3)((uint)uVar1 >> 8),1);
}



/* Address: 00404430 */

void FUN_00404430(undefined2 param_1,undefined2 param_2)

{
  if ((((DAT_00416215 == '\0') && (DAT_004162dc == 0)) && (DAT_00416769 == '\0')) &&
     ((DAT_00410080 != 0 && (DAT_00416770 != '\0')))) {
    DAT_004111b5._1_1_ = 1;
    FUN_00408b04(&DAT_00410074);
    DAT_0041007c = param_2;
    DAT_0041007e = param_1;
    FUN_00408b04(&DAT_00410074);
    FUN_004034fc();
  }
  return;
}



/* Address: 00404498 */

void FUN_00404498(void)

{
  undefined4 uVar1;
  undefined4 extraout_ECX;
  undefined4 extraout_EDX;
  
  FUN_0040a268();
  uVar1 = FUN_0040a260();
  DAT_004162b7 = 1;
  DAT_004162b5 = 1;
  DAT_004162b4 = 1;
  DAT_004162b6 = 1;
  DAT_00416761 = 1;
  DAT_004162b8._1_1_ = DAT_0041676e;
  uVar1 = FUN_0040a250(CONCAT31((int3)((uint)uVar1 >> 8),DAT_0041676e));
  DAT_004162b8._2_1_ = DAT_0041676f;
  FUN_0040a240(CONCAT31((int3)((uint)uVar1 >> 8),DAT_0041676f));
  FUN_00405d50(DAT_004162f4);
  FUN_0040a328();
  FUN_0040907f();
  FUN_004096c4();
  FUN_00403900();
  FUN_00405a88(extraout_ECX,extraout_EDX);
  return;
}



/* Address: 00404504 */

undefined4 FUN_00404504(void)

{
  FUN_0040bca8(0x4101cc);
  return 0;
}



/* Address: 00404514 */

void FUN_00404514(void)

{
  DAT_004161c4 = 0;
  DAT_004161c0 = 0;
  DAT_004115c0 = 0;
  DAT_004115bc = 0;
  DAT_004115c4 = 0;
  return;
}



/* Address: 00404530 */

void FUN_00404530(void)

{
  DAT_004115c4 = DAT_00416288 >> 2;
  DAT_004115bc = DAT_0041628c;
  DAT_004115c0 = DAT_0041628c;
  return;
}



/* Address: 00404550 */

int FUN_00404550(uint param_1)

{
  uint uVar1;
  
  if ((param_1 & 3) != 0) {
    param_1 = param_1 + 4;
  }
  uVar1 = param_1 >> 2;
  if (DAT_004115c4 <= uVar1) {
    FUN_00404504();
    return DAT_0041628c;
  }
  DAT_004115c4 = DAT_004115c4 - uVar1;
  DAT_004115c0 = DAT_004115c0 + uVar1 * 4;
  DAT_004115bc = DAT_004115c0;
  return DAT_004115c0;
}



/* Address: 00404594 */

uint * FUN_00404594(uint param_1)

{
  uint *puVar1;
  uint uVar2;
  uint uVar3;
  
  uVar2 = param_1 >> 2;
  if ((param_1 & 3) != 0) {
    uVar2 = uVar2 + 1;
  }
  uVar2 = uVar2 + 1;
  uVar3 = *DAT_004115bc;
  puVar1 = DAT_004115bc;
  while (((uVar3 & 0x80000000) != 0 || (uVar3 <= uVar2))) {
    if (DAT_004115c0 <= puVar1) {
      puVar1 = (uint *)FUN_00404504();
      return puVar1;
    }
    puVar1 = puVar1 + uVar3;
    uVar3 = *puVar1;
  }
  uVar3 = uVar3 - uVar2;
  if (uVar3 != 0) {
    puVar1[uVar2] = uVar3;
    if (puVar1 == DAT_004115c0) {
      DAT_004115c0 = puVar1 + uVar2;
      DAT_004115c4 = uVar3;
    }
  }
  *puVar1 = uVar2 | 0x80000000;
  return puVar1 + 1;
}



/* Address: 0040461c */

void FUN_0040461c(int param_1)

{
  uint *puVar1;
  uint uVar2;
  uint *puVar3;
  uint *puVar4;
  uint *puVar5;
  uint uVar6;
  
  puVar4 = (uint *)0x0;
  puVar3 = DAT_004115bc;
  if ((uint *)(param_1 + -4) != DAT_004115bc) {
    do {
      puVar4 = puVar3;
      puVar3 = puVar4 + *puVar4;
    } while (puVar3 != (uint *)(param_1 + -4));
  }
  uVar6 = *puVar3 & 0x7fffffff;
  puVar1 = puVar3 + uVar6;
  uVar2 = *puVar1;
  puVar5 = (uint *)0x0;
  if ((uVar2 & 0x80000000) == 0) {
    uVar6 = uVar6 + uVar2;
    puVar5 = puVar1;
  }
  if ((puVar4 != (uint *)0x0) && ((*puVar4 & 0x80000000) == 0)) {
    uVar6 = uVar6 + *puVar4;
    puVar3 = puVar4;
  }
  if (puVar5 == DAT_004115c0) {
    DAT_004115c0 = puVar3;
    DAT_004115c4 = uVar6;
  }
  *puVar3 = uVar6;
  return;
}



/* Address: 00404698 */

void FUN_00404698(void)

{
  uint uVar1;
  
  uVar1 = DAT_004161e0 * 4 + 0x14;
  if (uVar1 <= DAT_00416178) {
    uVar1 = DAT_00416178;
  }
  DAT_004115c8 = FUN_00404594(uVar1);
  return;
}



/* Address: 004046c0 */

void FUN_004046c0(void)

{
  FUN_0040461c(DAT_004115c8);
  return;
}



/* Address: 004046cc */

undefined * __fastcall FUN_004046cc(undefined4 param_1,undefined *param_2,uint param_3)

{
  if (param_3 < 0x99) {
    param_2 = &DAT_004115cc;
  }
  else if (DAT_004161c4 == 0) {
    if (DAT_004115c0 != 0) {
      param_2 = (undefined *)((DAT_004115c0 + DAT_004115c4 * 4) - param_3);
    }
  }
  else {
    param_2 = (undefined *)(DAT_004161c4 - param_3);
  }
  return param_2;
}



/* Address: 00404714 */

void FUN_00404714(int param_1)

{
  DAT_004161c4 = param_1;
  DAT_004115c4 = (param_1 - (int)DAT_004115c0) / 4 + -4;
  *DAT_004115c0 = DAT_004115c4;
  return;
}



/* Address: 00404740 */

void FUN_00404740(void)

{
  DAT_004161c0 = DAT_004115c0 + DAT_004115c4 * 4 + -0x10;
  FUN_00404714(DAT_004161c0);
  return;
}



/* Address: 00404760 */

byte * FUN_00404760(void)

{
  byte *pbVar1;
  
  pbVar1 = DAT_004161c0;
  if (((*DAT_004161c0 & 1) != 0) && (DAT_004161c0 != DAT_004161c4)) {
    do {
      pbVar1 = pbVar1 + -0x10;
      if ((*pbVar1 & 1) == 0) break;
    } while (pbVar1 != DAT_004161c4);
  }
  *pbVar1 = 1;
  if (pbVar1 == DAT_004161c4) {
    FUN_00404714((int)(pbVar1 + -0x10));
  }
  return pbVar1;
}



/* Address: 004047a4 */

void FUN_004047a4(byte *param_1)

{
  byte *pbVar1;
  
  *param_1 = 0;
  if (param_1 + -0x10 == DAT_004161c4) {
    do {
      pbVar1 = param_1;
      if (pbVar1 == DAT_004161c0) break;
      param_1 = pbVar1 + 0x10;
    } while ((pbVar1[0x10] & 1) == 0);
    FUN_00404714((int)pbVar1);
  }
  return;
}



/* Address: 004047e0 */

void FUN_004047e0(void)

{
  *DAT_004161c0 = 0;
  FUN_00404714((int)DAT_004161c0);
  return;
}



/* Address: 004047f4 */

void FUN_004047f4(int param_1)

{
  DAT_00411664 = 0;
  DAT_00411668 = 0;
  DAT_0041166c = param_1;
  if (param_1 != 0) {
    DAT_00411664 = DAT_004161f8;
  }
  return;
}



/* Address: 0040481c */

int FUN_0040481c(void)

{
  int iVar1;
  
  iVar1 = FUN_0040bce4();
  return iVar1 - DAT_00411668;
}



/* Address: 00404828 */

void FUN_00404828(DWORD param_1,int param_2)

{
  if (param_1 == 1) {
    param_2 = param_2 - DAT_00411668;
  }
  DAT_00411668 = 0;
  FUN_0040bcf8(param_1,param_2);
  return;
}



/* Address: 00404854 */

bool __fastcall FUN_00404854(DWORD param_1,char *param_2,uint param_3,char *param_4)

{
  bool bVar1;
  DWORD extraout_ECX;
  char *extraout_EDX;
  uint uVar2;
  
  do {
    if (DAT_00411668 == 0) {
      if ((DAT_00411664 <= param_3) || (DAT_0041166c == (char *)0x0)) {
        bVar1 = FUN_0040bd20(param_1,param_2,param_3,param_4);
        return bVar1;
      }
      DAT_00411664 = FUN_0040bd48(param_1,param_2,DAT_004161f8,DAT_0041166c);
      DAT_00411668 = DAT_00411664;
    }
    uVar2 = DAT_00411668;
    if (param_3 < DAT_00411668) {
      uVar2 = param_3;
    }
    FUN_0040c2a0((undefined4 *)param_4,(undefined4 *)(DAT_0041166c + (DAT_00411664 - DAT_00411668)),
                 uVar2);
    param_4 = param_4 + uVar2;
    DAT_00411668 = DAT_00411668 - uVar2;
    param_3 = param_3 - uVar2;
    param_1 = extraout_ECX;
    param_2 = extraout_EDX;
  } while (param_3 != 0);
  return true;
}



/* Address: 004048e4 */

void __fastcall FUN_004048e4(undefined4 param_1,undefined4 param_2)

{
  FUN_0040bd64(param_1,param_2);
  FUN_0040bca8(0x4101e4);
  return;
}



/* Address: 004048f4 */

void __fastcall FUN_004048f4(DWORD param_1,char *param_2)

{
  FUN_00404854(param_1,param_2,1,&DAT_00411684);
  return;
}



/* Address: 00404904 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 __fastcall FUN_00404904(DWORD param_1,char *param_2,uint param_3)

{
  char cVar1;
  bool bVar2;
  undefined4 uVar3;
  DWORD extraout_ECX;
  DWORD extraout_ECX_00;
  DWORD extraout_ECX_01;
  DWORD extraout_ECX_02;
  char *extraout_EDX;
  char *extraout_EDX_00;
  char *extraout_EDX_01;
  undefined8 uVar4;
  int local_8;
  char local_4;
  
  if (param_3 <= DAT_00411678) {
    cVar1 = FUN_00404828(2,-((DAT_00411678 - param_3) * 5 + _DAT_00411674 + 5));
    param_1 = extraout_ECX;
    param_2 = extraout_EDX;
    if (cVar1 != '\0') {
      bVar2 = FUN_00404854(extraout_ECX,extraout_EDX,5,(char *)&local_8);
      param_1 = extraout_ECX_00;
      param_2 = extraout_EDX_00;
      if ((bVar2) && (local_4 == (char)param_3)) {
        cVar1 = FUN_00404828(0,local_8 + _DAT_00411670);
        param_1 = extraout_ECX_01;
        param_2 = extraout_EDX_01;
        if (cVar1 != '\0') {
          uVar4 = FUN_004048f4(extraout_ECX_01,extraout_EDX_01);
          param_2 = (char *)((ulonglong)uVar4 >> 0x20);
          param_1 = extraout_ECX_02;
          if ((char)uVar4 != '\0') {
            return CONCAT31((int3)((ulonglong)uVar4 >> 8),1);
          }
        }
      }
    }
  }
  uVar3 = FUN_004048e4(param_1,param_2);
  return uVar3;
}



/* Address: 00404980 */

void __fastcall FUN_00404980(DWORD param_1,char *param_2,uint *param_3)

{
  longlong lVar1;
  char cVar2;
  bool bVar3;
  DWORD extraout_ECX;
  DWORD extraout_ECX_00;
  DWORD DVar4;
  char *extraout_EDX;
  char *extraout_EDX_00;
  char *pcVar5;
  uint uVar6;
  
  cVar2 = FUN_004048f4(param_1,param_2);
  DVar4 = extraout_ECX;
  pcVar5 = extraout_EDX;
  if (cVar2 != '\0') {
    if ((DAT_00411684 & 0x80) == 0) {
      *param_3 = (uint)DAT_00411684;
    }
    else {
      if ((DAT_00411684 & 0x20) == 0) {
        uVar6 = DAT_00411684 & 0x1f;
      }
      else {
        uVar6 = ((int)(uint)DAT_00411684 >> 2 & 3U) + 1;
      }
      *param_3 = 0;
      bVar3 = FUN_00404854(extraout_ECX,extraout_EDX,uVar6,(char *)param_3);
      DVar4 = extraout_ECX_00;
      pcVar5 = extraout_EDX_00;
      if (!bVar3) goto LAB_004049c3;
      DVar4 = CONCAT31((int3)(extraout_ECX_00 >> 8),DAT_00411684);
      if ((DAT_00411684 & 0x20) != 0) {
        lVar1 = (longlong)(1 << ((byte)(uVar6 << 3) & 0x1f)) * (longlong)(int)(DAT_00411684 & 3);
        pcVar5 = (char *)((ulonglong)lVar1 >> 0x20);
        *param_3 = *param_3 + (int)lVar1;
        DVar4 = *param_3;
        if ((DAT_00411684 & 0x10) == 0) {
          DVar4 = *(DWORD *)(&DAT_0041020c + uVar6 * 8);
          *param_3 = *param_3 + DVar4;
        }
        else {
          pcVar5 = (char *)(*(int *)(s_Invalid_game_data_file__004101e4 + uVar6 * 8 + 0x14) - DVar4)
          ;
          *param_3 = (uint)pcVar5;
        }
      }
    }
    FUN_004048f4(DVar4,pcVar5);
    return;
  }
LAB_004049c3:
  FUN_004048e4(DVar4,pcVar5);
  return;
}



/* Address: 00404a2c */

void __fastcall FUN_00404a2c(DWORD param_1,char *param_2,int *param_3)

{
  char cVar1;
  bool bVar2;
  uint in_EAX;
  int iVar3;
  undefined4 extraout_ECX;
  undefined4 uVar4;
  undefined4 extraout_ECX_00;
  DWORD extraout_ECX_01;
  char *extraout_EDX;
  char *extraout_EDX_00;
  char *pcVar5;
  char *extraout_EDX_01;
  uint local_4;
  
  local_4 = in_EAX;
  cVar1 = FUN_004048f4(param_1,param_2);
  uVar4 = extraout_ECX;
  pcVar5 = extraout_EDX;
  if ((cVar1 != '\0') &&
     (uVar4 = CONCAT31((int3)((uint)extraout_ECX >> 8),0xec), (DAT_00411684 & 0xec) == 0xec)) {
    local_4 = (DAT_00411684 & 3) + 1;
    bVar2 = FUN_00404854((DWORD)&local_4,extraout_EDX,local_4,(char *)&local_4);
    uVar4 = extraout_ECX_00;
    pcVar5 = extraout_EDX_00;
    if (bVar2) {
      iVar3 = FUN_0040481c();
      *param_3 = iVar3;
      FUN_00404828(1,local_4);
      FUN_004048f4(extraout_ECX_01,extraout_EDX_01);
      return;
    }
  }
  FUN_004048e4(uVar4,pcVar5);
  return;
}



/* Address: 00404a8c */

void __fastcall FUN_00404a8c(DWORD param_1,char *param_2,byte *param_3)

{
  char cVar1;
  bool bVar2;
  byte *pbVar3;
  char *extraout_ECX;
  char *extraout_ECX_00;
  char *pcVar4;
  char *extraout_ECX_01;
  char *extraout_ECX_02;
  char *extraout_ECX_03;
  char *extraout_ECX_04;
  char *extraout_EDX;
  char *extraout_EDX_00;
  char *pcVar5;
  char *extraout_EDX_01;
  char *extraout_EDX_02;
  char *extraout_EDX_03;
  char *extraout_EDX_04;
  char *extraout_EDX_05;
  char *extraout_EDX_06;
  char *local_c;
  char *local_8;
  uint local_4;
  
  DAT_00411680 = (char *)0x0;
  DAT_0041167c = (char *)0x0;
  cVar1 = FUN_004048f4(param_1,param_2);
  pcVar4 = extraout_ECX;
  pcVar5 = extraout_EDX;
  if ((cVar1 != '\0') && ((DAT_00411684 & 0x80) != 0)) {
    if ((DAT_00411684 & 0x40) != 0) {
      DAT_0041167c = (char *)(DAT_00411684 & 0x1f);
      pcVar4 = extraout_ECX;
      if ((DAT_00411684 & 0x1f) != 0) {
        bVar2 = FUN_00404854((DWORD)extraout_ECX,extraout_EDX,(uint)DAT_0041167c,(char *)param_3);
        pcVar4 = extraout_ECX_00;
        pcVar5 = extraout_EDX_00;
        if (!bVar2) goto LAB_00404b8a;
      }
LAB_00404adf:
      FUN_004048f4((DWORD)pcVar4,pcVar5);
      return;
    }
    local_c = DAT_0041022c;
    local_8 = DAT_00410230;
    local_4 = (DAT_00411684 & 3) + 1;
    bVar2 = FUN_00404854((DWORD)extraout_ECX,DAT_00410230,local_4,(char *)&local_c);
    pcVar4 = extraout_ECX_01;
    pcVar5 = extraout_EDX_01;
    if (bVar2) {
      if ((DAT_00411684 & 0xc) == 0) {
        DAT_0041167c = local_c;
        bVar2 = FUN_00404854((DWORD)extraout_ECX_01,extraout_EDX_01,(uint)local_c,(char *)param_3);
        pcVar4 = extraout_ECX_04;
        pcVar5 = extraout_EDX_06;
        if (bVar2) goto LAB_00404adf;
      }
      else {
        pbVar3 = FUN_004046cc(extraout_ECX_01,extraout_EDX_01,(uint)local_c);
        bVar2 = FUN_00404854((DWORD)&local_8,extraout_EDX_02,local_4,(char *)&local_8);
        pcVar4 = extraout_ECX_02;
        pcVar5 = extraout_EDX_03;
        if (bVar2) {
          bVar2 = FUN_00404854((DWORD)extraout_ECX_02,extraout_EDX_03,(uint)local_c,(char *)pbVar3);
          pcVar4 = extraout_ECX_03;
          pcVar5 = extraout_EDX_04;
          if (bVar2) {
            DAT_00411680 = local_c;
            FUN_00409630((int)local_c,param_3,pbVar3);
            DAT_0041167c = local_8;
            pcVar4 = local_8;
            pcVar5 = extraout_EDX_05;
            goto LAB_00404adf;
          }
        }
      }
    }
  }
LAB_00404b8a:
  FUN_004048e4(pcVar4,pcVar5);
  return;
}



/* Address: 00404b98 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_00404b98(LPCSTR param_1)

{
  char cVar1;
  bool bVar2;
  uint uVar3;
  undefined4 uVar4;
  undefined4 extraout_ECX;
  undefined4 extraout_ECX_00;
  DWORD extraout_ECX_01;
  undefined4 extraout_ECX_02;
  undefined4 extraout_ECX_03;
  DWORD extraout_ECX_04;
  undefined4 extraout_ECX_05;
  undefined4 extraout_EDX;
  undefined4 extraout_EDX_00;
  undefined4 extraout_EDX_01;
  undefined4 extraout_EDX_02;
  undefined4 extraout_EDX_03;
  undefined4 uVar5;
  undefined8 uVar6;
  DWORD local_c4;
  char local_bd;
  int local_bc;
  uint local_b8;
  uint local_b4;
  
  DAT_0041675f = 0;
  FUN_004047f4(0);
  uVar3 = FUN_0040bcc0(param_1);
  uVar4 = extraout_ECX;
  uVar5 = extraout_EDX;
  if ((char)uVar3 != '\0') {
    cVar1 = FUN_00404828(2,-0xc);
    uVar4 = extraout_ECX_00;
    uVar5 = extraout_EDX_00;
    if (cVar1 != '\0') {
      uVar6 = FUN_00408e4c(0xc,(undefined1 *)&local_c4);
      bVar2 = FUN_0040bd20(extraout_ECX_01,(char *)((ulonglong)uVar6 >> 0x20),0xc,(char *)uVar6);
      uVar4 = extraout_ECX_02;
      uVar5 = extraout_EDX_01;
      if (((bVar2) && (local_bd == '\x04')) && (local_bc == 0x3d53563d)) {
        _DAT_00411674 = local_c4 + 0xc;
        cVar1 = FUN_00404828(2,-_DAT_00411674);
        uVar4 = extraout_ECX_03;
        uVar5 = extraout_EDX_02;
        if (cVar1 != '\0') {
          uVar6 = FUN_00408e4c(0xb8,(undefined1 *)&local_b8);
          bVar2 = FUN_0040bd20(extraout_ECX_04,(char *)((ulonglong)uVar6 >> 0x20),local_c4,
                               (char *)uVar6);
          uVar4 = extraout_ECX_05;
          uVar5 = extraout_EDX_03;
          if ((bVar2) && (_DAT_00411670 = local_b4, local_b4 < 0x13881)) {
            DAT_00411678 = local_b8;
            if (local_b8 < 0xff) {
              return CONCAT31((int3)(local_b8 >> 8),1);
            }
          }
        }
      }
    }
  }
  uVar4 = FUN_004048e4(uVar4,uVar5);
  return uVar4;
}



/* Address: 00404c64 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00404c64(int param_1)

{
  byte bVar1;
  uint *puVar2;
  int iVar3;
  byte *pbVar4;
  int iVar5;
  byte *local_14;
  undefined4 *local_10;
  short local_c;
  
  iVar5 = param_1 * 0x10 + _DAT_004161e8;
  *(undefined4 *)(iVar5 + 0xc) = 0;
  local_14 = DAT_004161c0;
  if (DAT_004161c4 < DAT_004161c0) {
    do {
      bVar1 = *local_14 & 3;
      if (bVar1 == 1) {
        if ((*local_14 & 4) == 0) goto LAB_00404cb6;
      }
      else if (bVar1 == 3) {
LAB_00404cb6:
        *(int *)(iVar5 + 0xc) = *(int *)(iVar5 + 0xc) + 1;
      }
      local_14 = local_14 + -0x10;
    } while (DAT_004161c4 < local_14);
  }
  puVar2 = FUN_00404594(*(int *)(iVar5 + 0xc) * 0x10 + DAT_004161e0 * 2);
  *(uint **)(iVar5 + 8) = puVar2;
  param_1 = 0;
  iVar3 = 0;
  do {
    FUN_0040c2a0((undefined4 *)(iVar3 * DAT_00416168 + *(int *)(iVar5 + 8)),
                 (undefined4 *)(DAT_004161d0 + (param_1 << ((byte)DAT_004161d4 & 0x1f)) * 2),
                 DAT_00416168 * 2);
    iVar3 = iVar3 + 2;
    param_1 = param_1 + 1;
  } while (param_1 < DAT_0041616c);
  local_10 = (undefined4 *)(*(int *)(iVar5 + 8) + DAT_004161e0 * 2);
  param_1 = 0;
  local_14 = DAT_004161c0;
  if (DAT_004161c4 < DAT_004161c0) {
    do {
      bVar1 = *local_14 & 3;
      if (bVar1 == 3) {
        local_14[10] = 0xff;
        local_14[0xb] = 0xff;
        local_c = (short)param_1;
        if (*(ushort *)(local_14 + 8) == 0xffff) {
          *local_10 = *(undefined4 *)local_14;
          local_10[1] = *(undefined4 *)(local_14 + 4);
          local_10[2] = *(undefined4 *)(local_14 + 8);
          local_10[3] = *(undefined4 *)(local_14 + 0xc);
          local_10 = local_10 + 4;
          *local_14 = 0;
          param_1 = param_1 + 1;
        }
        else {
          pbVar4 = DAT_004161c0 + (uint)*(ushort *)(local_14 + 8) * -0x10;
          *(short *)(local_14 + 8) = local_c + 1;
          *(short *)(local_14 + 10) = local_c + 1;
          *local_10 = *(undefined4 *)local_14;
          local_10[1] = *(undefined4 *)(local_14 + 4);
          local_10[2] = *(undefined4 *)(local_14 + 8);
          local_10[3] = *(undefined4 *)(local_14 + 0xc);
          *local_14 = 0;
          *(short *)(pbVar4 + 6) = local_c;
          *pbVar4 = *pbVar4 | 0x80;
          local_10[4] = *(undefined4 *)pbVar4;
          local_10[5] = *(undefined4 *)(pbVar4 + 4);
          local_10[6] = *(undefined4 *)(pbVar4 + 8);
          local_10[7] = *(undefined4 *)(pbVar4 + 0xc);
          local_10 = local_10 + 8;
          *pbVar4 = 0;
          param_1 = param_1 + 2;
        }
        iVar5 = (int)DAT_004161c0 - (int)local_14;
        pbVar4 = DAT_004161c0;
        if (DAT_004161c4 < DAT_004161c0) {
          do {
            if (((*pbVar4 & 7) == 1) && ((uint)*(ushort *)(pbVar4 + 6) == iVar5 / 0x10)) {
              *(short *)(pbVar4 + 6) = local_c;
              *local_10 = *(undefined4 *)pbVar4;
              local_10[1] = *(undefined4 *)(pbVar4 + 4);
              local_10[2] = *(undefined4 *)(pbVar4 + 8);
              local_10[3] = *(undefined4 *)(pbVar4 + 0xc);
              local_10 = local_10 + 4;
              *pbVar4 = 0;
              param_1 = param_1 + 1;
            }
            pbVar4 = pbVar4 + -0x10;
          } while (DAT_004161c4 < pbVar4);
        }
      }
      else if (((bVar1 == 1) && (*(short *)(local_14 + 6) == -1)) && ((*local_14 & 4) == 0)) {
        *local_10 = *(undefined4 *)local_14;
        local_10[1] = *(undefined4 *)(local_14 + 4);
        local_10[2] = *(undefined4 *)(local_14 + 8);
        local_10[3] = *(undefined4 *)(local_14 + 0xc);
        local_10 = local_10 + 4;
        *local_14 = 0;
        param_1 = param_1 + 1;
      }
      local_14 = local_14 + -0x10;
    } while (DAT_004161c4 < local_14);
  }
  FUN_004047e0();
  return;
}



/* Address: 00404ec8 */

void FUN_00404ec8(undefined4 *param_1)

{
  FUN_0040c2a0(param_1,DAT_00411688,DAT_0041167c);
  return;
}



/* Address: 00404ee4 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 __fastcall FUN_00404ee4(DWORD param_1,char *param_2)

{
  bool bVar1;
  undefined4 uVar2;
  undefined4 extraout_ECX;
  undefined4 extraout_EDX;
  
  FUN_00404a8c(param_1,param_2,(byte *)&DAT_004161f0);
  if (DAT_004161f0 != -0x7fffffea) {
    uVar2 = FUN_004048e4(extraout_ECX,extraout_EDX);
    return uVar2;
  }
  *(undefined1 *)((int)&DAT_004161ec + DAT_0041167c + 3) = 0;
  DAT_00416288 = DAT_004161f4 << 10;
  _DAT_00416290 = DAT_004161fc * DAT_00416200;
  bVar1 = FUN_0040b664(&DAT_004162a0,0x4161f0,(SIZE_T *)&DAT_00416288);
  if (bVar1) {
    uVar2 = FUN_00409080(0x4161f0,&DAT_00416288);
    if ((char)uVar2 != '\0') {
      FUN_00404514();
      DAT_00416338 = FUN_00404530();
      DAT_00411688 = FUN_00404550(DAT_0041620c << 2);
      if (DAT_004161f8 != 0) {
        DAT_0041168c = DAT_00411688;
        FUN_004047f4(DAT_00411688);
        DAT_00411688 = FUN_00404550(DAT_004161f8);
      }
      DAT_0041168c = DAT_00411688;
      return CONCAT31((int3)((uint)DAT_00411688 >> 8),1);
    }
  }
  return 0;
}



/* Address: 00404fb0 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void __fastcall FUN_00404fb0(DWORD param_1,char *param_2)

{
  undefined1 *puVar1;
  int *piVar2;
  int iVar3;
  
  FUN_00404a8c(param_1,param_2,(byte *)&DAT_00416160);
  DAT_004161e4 = DAT_00416160 * DAT_00416164;
  DAT_004161e0 = DAT_00416168 * DAT_0041616c;
  DAT_004161ec = DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_00416178);
  iVar3 = 0;
  if (DAT_00410234 < DAT_00416168) {
    piVar2 = &DAT_00410234;
    do {
      piVar2 = piVar2 + 2;
      iVar3 = iVar3 + 1;
    } while (*piVar2 < DAT_00416168);
  }
  DAT_004161d4 = (&DAT_00410238)[iVar3 * 2];
  DAT_004161d0 = (int)DAT_00411688;
  puVar1 = (undefined1 *)FUN_00404550(DAT_0041616c << ((char)DAT_004161d4 + 1U & 0x1f));
  DAT_00411688 = puVar1;
  _DAT_004161e8 = puVar1;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_004161e4 << 4);
  DAT_00416340 = DAT_00416168 + -1;
  DAT_00416344 = -DAT_00416168;
  DAT_00416348 = DAT_0041616c + -1;
  DAT_0041633c = (int)DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_004161e0);
  DAT_004161c8 = (int)DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_0041617c << 4);
  DAT_004161cc = (int)DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_00416180 << 4);
  DAT_004161dc = (int)DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_00416184 << 3);
  DAT_004161d8 = (int)DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_00416188 << 3);
  DAT_0041629c = (int)DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_0041618c << 2);
  DAT_00416298 = (int)DAT_00411688;
  DAT_00411688 = (undefined1 *)FUN_00404550(DAT_00416190 << 3);
  FUN_00408e4c((int)DAT_00411688 - (int)puVar1,puVar1);
  DAT_0041168c = (int)DAT_00411688;
  DAT_0041634c = (int)DAT_00411688;
  return;
}



/* Address: 00405150 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_00405150(uint *param_1,int param_2,int param_3,int param_4,int param_5,int param_6)

{
  char *pcVar1;
  uint *puVar2;
  uint *puVar3;
  byte bVar4;
  undefined2 *puVar5;
  int iVar6;
  
  bVar4 = DAT_00416213;
  pcVar1 = DAT_004161c8;
  if (param_6 == 0x12) {
    DAT_004162e0 = param_1;
    *(byte *)((int)param_1 + 1) = DAT_00416213;
    param_6 = FUN_0040b754(bVar4);
  }
  else if ((param_6 != 0x11) && (param_6 != 0x13)) {
    if (param_6 == 0x18) {
      param_6 = param_5 * 0x10 + _DAT_004161e8;
      *(undefined4 *)param_6 = DAT_004161ec;
      *(undefined4 *)(param_6 + 4) = DAT_00411690;
      *(char *)(param_6 + 8) = '\0';
      *(char *)(param_6 + 9) = '\0';
      *(char *)(param_6 + 10) = '\0';
      *(char *)(param_6 + 0xb) = '\0';
      *(char *)(param_6 + 0xc) = '\0';
      *(char *)(param_6 + 0xd) = '\0';
      *(char *)(param_6 + 0xe) = '\0';
      *(char *)(param_6 + 0xf) = '\0';
      if ((DAT_00411694 == -1) && ((*param_1 & 0x80000000) != 0)) {
        DAT_00411694 = param_5;
      }
    }
    else if (param_6 == 0x19) {
      if (DAT_0041675f == '\0') {
        iVar6 = DAT_0041616c << ((byte)DAT_004161d4 & 0x1f);
        puVar5 = DAT_004161d0;
        do {
          *puVar5 = 0xf0fe;
          iVar6 = iVar6 + -1;
          puVar5 = puVar5 + 1;
        } while (iVar6 != 0);
        DAT_00416760 = 1;
        FUN_00403a64('\x01',param_1);
        DAT_00416760 = 0;
        param_6 = FUN_00404c64(param_5);
      }
    }
    else if (param_6 == 0x20) {
      param_6 = FUN_00404ec8((undefined4 *)(DAT_004161c8 + param_5 * 0x10));
    }
    else if (param_6 == 0x21) {
      *(int *)(DAT_004161c8 + param_5 * 0x10 + 4) = (int)param_1 + param_2;
      param_6 = (int)pcVar1;
    }
    else if (param_6 == 0x22) {
      *(int *)(DAT_004161c8 + param_5 * 0x10 + 0xc) = (int)param_1 + param_2;
      param_6 = (int)pcVar1;
    }
    else if (param_6 == 0x23) {
      *(uint **)(DAT_004161c8 + param_5 * 0x10 + 8) = param_1;
    }
    else if (param_6 == 0x2a) {
      *(uint **)(*(int *)(DAT_004161c8 + param_5 * 0x10 + 8) + param_4 * 4) = param_1;
      param_6 = (int)pcVar1;
    }
    else if (param_6 == 0x2b) {
      param_6 = *(int *)(DAT_004161c8 + param_5 * 0x10 + 8);
      *(int *)(*(int *)(param_6 + param_4 * 4) + 4) = (int)param_1 + param_2;
    }
    else if (param_6 == 0x2c) {
      puVar3 = *(uint **)(*(int *)(DAT_004161c8 + param_5 * 0x10 + 8) + param_4 * 4);
      iVar6 = 1;
      if ((*puVar3 & 1) != 0) {
        iVar6 = 2;
      }
      if ((*puVar3 & 2) != 0) {
        do {
          puVar2 = puVar3 + iVar6;
          iVar6 = iVar6 + 1 + (*puVar2 & 0xffff);
        } while ((*puVar2 & 0x10000) == 0);
      }
      puVar3[iVar6] = (int)param_1 + param_2;
      param_6 = (int)pcVar1;
    }
    else if (param_6 == 0x38) {
      *(uint **)(param_5 * 0x10 + DAT_004161cc) = param_1;
    }
    else if (param_6 == 0x39) {
      *(uint **)(param_5 * 0x10 + 4 + DAT_004161cc) = param_1;
    }
    else if (param_6 == 0x3a) {
      *(uint **)(param_5 * 0x10 + 8 + DAT_004161cc) = param_1;
    }
    else if (param_6 == 0x40) {
      FUN_00404ec8((undefined4 *)(DAT_004161dc + param_5 * 8));
      param_6 = CONCAT31((int3)((uint)DAT_00411688 >> 8),*(undefined1 *)(DAT_00411688 + 1)) &
                0xffffff80;
      DAT_00411699 = (undefined1)param_6;
    }
    else if (param_6 == 0x41) {
      *(uint **)(DAT_004161dc + 4 + param_5 * 8) = param_1;
    }
    else if (param_6 == 0x42) {
      pcVar1 = (char *)(DAT_004161dc + param_5 * 8);
      if ((*pcVar1 == '\0') && (1 < (byte)pcVar1[2])) {
        *(uint **)(*(int *)(pcVar1 + 4) + param_4 * 4) = param_1;
        param_6 = param_4;
      }
      else {
        *(uint **)(pcVar1 + 4) = param_1;
      }
    }
    else if (param_6 == 0x43) {
      param_6 = DAT_004161dc + param_5 * 8;
      iVar6 = *(int *)(param_6 + 4);
      if ((*(char *)param_6 == '\0') && (1 < *(byte *)(param_6 + 2))) {
        iVar6 = *(int *)(iVar6 + param_4 * 4);
      }
      *(uint **)(iVar6 + param_3 * 8) = param_1;
    }
    else if (param_6 == 0x50) {
      param_6 = FUN_00404ec8((undefined4 *)(DAT_004161d8 + param_5 * 8));
    }
    else if (param_6 == 0x51) {
      *(uint **)(DAT_004161d8 + 4 + param_5 * 8) = param_1;
    }
    else if (param_6 == 0x52) {
      *(uint **)(DAT_0041629c + param_5 * 4) = param_1;
    }
    else if (param_6 == 0x60) {
      *(uint **)(DAT_00416298 + param_5 * 8) = param_1;
    }
    else if (param_6 == 0x61) {
      *(uint **)(DAT_00416298 + 4 + param_5 * 8) = param_1;
    }
    else if (param_6 == 0x7c) {
      DAT_004162e4 = param_1;
    }
    else if (param_6 == 0x7d) {
      DAT_004162e8 = param_1;
    }
    else if (param_6 == 0x7e) {
      DAT_004162ec = param_1;
    }
  }
  return CONCAT31((int3)((uint)param_6 >> 8),1);
}



/* Address: 00405514 */

undefined4 FUN_00405514(char *param_1)

{
  char *pcVar1;
  char cVar2;
  undefined4 uVar3;
  int iVar4;
  DWORD extraout_ECX;
  DWORD extraout_ECX_00;
  DWORD DVar5;
  char *pcVar6;
  char *extraout_ECX_01;
  char *extraout_ECX_02;
  char *extraout_ECX_03;
  char *extraout_ECX_04;
  char *extraout_ECX_05;
  DWORD extraout_ECX_06;
  char *extraout_ECX_07;
  undefined *puVar7;
  char *extraout_ECX_08;
  char *extraout_ECX_09;
  char *extraout_ECX_10;
  char *extraout_ECX_11;
  uint uVar8;
  char *extraout_ECX_12;
  char *extraout_ECX_13;
  char *extraout_ECX_14;
  char *extraout_EDX;
  char *extraout_EDX_00;
  char *extraout_EDX_01;
  char *pcVar9;
  char *extraout_EDX_02;
  char *extraout_EDX_03;
  char *extraout_EDX_04;
  char *extraout_EDX_05;
  char *extraout_EDX_06;
  char *extraout_EDX_07;
  char *extraout_EDX_08;
  char *extraout_EDX_09;
  char *extraout_EDX_10;
  char *extraout_EDX_11;
  char *extraout_EDX_12;
  char *extraout_EDX_13;
  char *extraout_EDX_14;
  char *extraout_EDX_15;
  char *extraout_EDX_16;
  uint uVar10;
  uint unaff_ESI;
  char *pcVar11;
  char *local_4c;
  int local_48;
  int local_44;
  int local_40;
  char *local_3c;
  undefined local_38 [52];
  char *local_4;
  
  FUN_004047f4(0);
  DVar5 = extraout_ECX;
  pcVar6 = extraout_EDX;
  if (((DAT_00416761 == '\0') &&
      (uVar3 = FUN_00405d50(DAT_004162f4), DVar5 = extraout_ECX_00, pcVar6 = extraout_EDX_00,
      (char)uVar3 == '\0')) || (uVar3 = FUN_00404904(DVar5,pcVar6,1), (char)uVar3 == '\0')) {
    return 0;
  }
  DAT_00411698 = '\x01';
  DAT_004162d8 = 0xffffffff;
  DAT_00411694 = -1;
  DAT_004162f0 = 0;
  DAT_004162d4 = param_1;
  DAT_004162ec = 0;
  DAT_004162e8 = 0;
  DAT_004162e4 = 0;
  DAT_004116a0 = 0;
  DAT_0041169c = 0;
  DAT_004116a4 = (uint *)0x0;
  FUN_00406958();
  FUN_0040a268();
  local_4 = param_1 + 2;
  pcVar6 = local_4;
  pcVar9 = extraout_EDX_01;
  pcVar11 = (char *)0x1;
LAB_004055b6:
  do {
    while (pcVar1 = local_4, DAT_00411684 == 0) {
      if (pcVar11 != (char *)0x1) {
        FUN_004046c0();
        if (DAT_0041675f == '\0') {
          uVar3 = FUN_00403484('\0',0);
          if (((char)uVar3 != '\0') && (uVar3 = FUN_00405c58(DAT_004162f4), (char)uVar3 != '\0')) {
            iVar4 = DAT_00411694;
            if (DAT_00411694 == -1) {
              iVar4 = 0;
            }
            uVar3 = FUN_004058f8('\0',iVar4);
            if ((char)uVar3 != '\0') goto LAB_0040564b;
          }
          uVar3 = 0;
        }
        else {
LAB_0040564b:
          uVar3 = 1;
        }
        return uVar3;
      }
      uVar3 = FUN_00404904((DWORD)pcVar6,pcVar9,(uint)local_4);
      pcVar6 = extraout_ECX_01;
      pcVar9 = extraout_EDX_02;
      pcVar11 = pcVar1;
      if ((char)uVar3 == '\0') {
        return 0;
      }
    }
    if (DAT_00411684 != 1) {
      if (DAT_00411684 == 2) {
        FUN_00404980((DWORD)&local_44,pcVar9,(uint *)&local_44);
        pcVar6 = extraout_ECX_03;
        pcVar9 = extraout_EDX_04;
        goto LAB_004055b6;
      }
      if (DAT_00411684 == 3) {
        FUN_00404980((DWORD)&local_40,pcVar9,(uint *)&local_40);
        pcVar6 = extraout_ECX_04;
        pcVar9 = extraout_EDX_05;
      }
      else {
        if (DAT_00411684 != 5) {
          if (DAT_00411684 == 0xff) {
            uVar3 = FUN_00405150(DAT_0041168c,local_40,local_44,local_48,(int)local_4c,unaff_ESI);
            if ((char)uVar3 == '\0') {
              return 0;
            }
            cVar2 = FUN_004048f4(extraout_ECX_06,extraout_EDX_07);
            pcVar6 = extraout_ECX_07;
            pcVar9 = extraout_EDX_08;
joined_r0x004056f2:
            if (cVar2 == '\0') {
              return 0;
            }
            goto LAB_004055b6;
          }
          if (DAT_00411684 != 4) {
            local_40 = 0;
            local_44 = 0;
            local_48 = 0;
            local_4c = (char *)0x0;
            unaff_ESI = DAT_00411684 & 0xffffff7f;
            if ((DAT_00411684 & 0x80) == 0) {
              FUN_00404980((DWORD)&local_4c,pcVar9,(uint *)&local_4c);
              pcVar6 = extraout_ECX_13;
              pcVar9 = extraout_EDX_15;
            }
            else {
              FUN_004048f4((uint)DAT_00411684,pcVar9);
              pcVar6 = extraout_ECX_14;
              pcVar9 = extraout_EDX_16;
            }
            goto LAB_004055b6;
          }
          switch(unaff_ESI) {
          case 0xf:
            if ((DAT_00416761 == '\0') || (DAT_0041675f != '\0')) {
              puVar7 = local_38;
            }
            else {
              puVar7 = &DAT_004162a0;
            }
            FUN_00404a8c((DWORD)puVar7,pcVar9,puVar7);
            pcVar6 = extraout_ECX_08;
            pcVar9 = extraout_EDX_09;
            goto LAB_004055b6;
          case 0x10:
            uVar3 = FUN_00404ee4((DWORD)pcVar6,pcVar9);
            cVar2 = (char)uVar3;
            pcVar6 = extraout_ECX_09;
            pcVar9 = extraout_EDX_10;
            goto joined_r0x004056f2;
          case 0x11:
            if (local_4c == param_1) {
              FUN_00404fb0((DWORD)local_4c,pcVar9);
              pcVar6 = extraout_ECX_10;
              pcVar9 = extraout_EDX_11;
              goto LAB_004055b6;
            }
            break;
          case 0x18:
            if (DAT_00411698 != '\0') {
              DAT_00411698 = '\0';
              if (DAT_004116a0 != 0) {
                DAT_004116a4 = DAT_00411688;
                FUN_00404550(DAT_004116a0);
              }
              FUN_00403908();
              FUN_00404740();
              DAT_00411688 = (uint *)FUN_00404698();
            }
            DAT_00411690 = FUN_0040481c();
            DAT_004162f0 = DAT_004162f0 + 1;
            pcVar9 = extraout_EDX_12;
          }
          DAT_0041168c = DAT_00411688;
          FUN_00404a8c((DWORD)DAT_00411688,pcVar9,(byte *)DAT_00411688);
          uVar3 = FUN_00405150(DAT_00411688,local_40,local_44,local_48,(int)local_4c,unaff_ESI);
          if ((char)uVar3 == '\0') {
            return 0;
          }
          if (unaff_ESI == 0x43) {
            if (DAT_00411699 != '\0') {
              uVar8 = DAT_0041167c - 4;
              uVar10 = DAT_0041167c - 4;
              if (DAT_00411680 != 0) {
                uVar8 = DAT_0041167c;
                uVar10 = DAT_00411680;
              }
              if (DAT_004116a0 < uVar8) {
                DAT_004116a0 = uVar8;
              }
              DAT_00411688[1] = 0x4469736b;
              iVar4 = FUN_0040481c();
              DAT_00411688[2] = (iVar4 - uVar10) - 1;
              DAT_00411688[3] = DAT_00411680;
              DAT_0041167c = 0x10;
            }
          }
          else {
            pcVar6 = extraout_ECX_11;
            pcVar9 = extraout_EDX_13;
            if (((unaff_ESI == 0x11) ||
                (pcVar6 = extraout_ECX_11, pcVar9 = extraout_EDX_13, unaff_ESI == 0x20)) ||
               ((pcVar6 = extraout_ECX_11, pcVar9 = extraout_EDX_13, unaff_ESI == 0x40 ||
                (((pcVar6 = extraout_ECX_11, pcVar9 = extraout_EDX_13, unaff_ESI == 0x50 ||
                  (pcVar6 = extraout_ECX_11, pcVar9 = extraout_EDX_13, unaff_ESI == 0x18)) ||
                 (pcVar6 = extraout_ECX_11, pcVar9 = extraout_EDX_13, unaff_ESI == 0x19))))))
            goto LAB_004055b6;
          }
          DAT_00411688 = (uint *)FUN_00404550(DAT_0041167c);
          pcVar6 = extraout_ECX_12;
          pcVar9 = extraout_EDX_14;
          goto LAB_004055b6;
        }
        FUN_00404a2c((DWORD)&local_3c,pcVar9,(int *)&local_3c);
        pcVar6 = extraout_ECX_05;
        pcVar9 = extraout_EDX_06;
        if (unaff_ESI == 0x14) {
          *(char **)(DAT_00416338 + (int)local_4c * 4) = local_3c;
          pcVar6 = local_3c;
          pcVar9 = local_4c;
        }
      }
      goto LAB_004055b6;
    }
    FUN_00404980((DWORD)&local_48,pcVar9,(uint *)&local_48);
    pcVar6 = extraout_ECX_02;
    pcVar9 = extraout_EDX_03;
  } while( true );
}



/* Address: 004058f8 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_004058f8(char param_1,int param_2)

{
  undefined4 *puVar1;
  char cVar2;
  undefined4 *puVar3;
  byte *pbVar4;
  undefined4 uVar5;
  DWORD extraout_ECX;
  char *extraout_EDX;
  int iVar6;
  int iVar7;
  
  if ((DAT_004162d8 != -1) && (param_1 == '\0')) {
    FUN_00404c64(DAT_004162d8);
  }
  DAT_004162d8 = param_2;
  puVar3 = (undefined4 *)(param_2 * 0x10 + _DAT_004161e8);
  cVar2 = FUN_00404828(0,puVar3[1]);
  if ((cVar2 != '\0') &&
     (cVar2 = FUN_00404a8c(extraout_ECX,extraout_EDX,(byte *)*puVar3), cVar2 != '\0')) {
    iVar6 = 0;
    iVar7 = 0;
    do {
      FUN_0040c2a0((undefined4 *)(DAT_004161d0 + (iVar6 << ((byte)DAT_004161d4 & 0x1f)) * 2),
                   (undefined4 *)(iVar7 * DAT_00416168 + puVar3[2]),DAT_00416168 * 2);
      iVar7 = iVar7 + 2;
      iVar6 = iVar6 + 1;
    } while (iVar6 < DAT_0041616c);
    puVar1 = (undefined4 *)(puVar3[2] + DAT_004161e0 * 2);
    for (iVar6 = puVar3[3]; iVar6 != 0; iVar6 = iVar6 + -1) {
      pbVar4 = FUN_00404760();
      *(undefined4 *)pbVar4 = *puVar1;
      *(undefined4 *)(pbVar4 + 4) = puVar1[1];
      *(undefined4 *)(pbVar4 + 8) = puVar1[2];
      *(undefined4 *)(pbVar4 + 0xc) = puVar1[3];
      puVar1 = puVar1 + 4;
    }
    puVar3[3] = 0;
    FUN_0040461c(puVar3[2]);
    FUN_00403948('\0');
    if ((param_1 == '\0') && (uVar5 = FUN_004043f0(), (char)uVar5 == '\0')) {
      uVar5 = 0;
    }
    else {
      uVar5 = 1;
    }
    return uVar5;
  }
  return 0;
}



/* Address: 00405a08 */

ushort * FUN_00405a08(ushort *param_1)

{
  uint uVar1;
  int iVar2;
  byte *pbVar3;
  undefined4 extraout_ECX;
  DWORD extraout_ECX_00;
  undefined *extraout_EDX;
  char *extraout_EDX_00;
  
  if (*(int *)(param_1 + 2) == 0x4469736b) {
    uVar1 = *(uint *)(param_1 + 6);
    iVar2 = *(int *)(param_1 + 4);
    if (iVar2 != DAT_0041169c) {
      DAT_0041169c = iVar2;
      FUN_00404828(0,iVar2);
      if (uVar1 == 0) {
        FUN_0040bd20((uint)param_1[1],(char *)0x0,(uint)*param_1 * (uint)param_1[1],
                     (char *)DAT_004116a4);
      }
      else {
        pbVar3 = FUN_004046cc(extraout_ECX,extraout_EDX,uVar1);
        FUN_0040bd20(extraout_ECX_00,extraout_EDX_00,uVar1,(char *)pbVar3);
        FUN_00409630(uVar1,DAT_004116a4,pbVar3);
      }
    }
    return (ushort *)(DAT_004116a4 + (-(uint)(uVar1 == 0) & 0xfffffffc) + 4);
  }
  return param_1 + 2;
}



/* Address: 00405a88 */

void __fastcall FUN_00405a88(undefined4 param_1,undefined4 param_2)

{
  FUN_0040bd64(param_1,param_2);
  return;
}



/* Address: 00405a90 */

void FUN_00405a90(uint param_1,int param_2,byte *param_3)

{
  do {
    param_1 = param_1 + 1;
    *param_3 = *param_3 ^ (&DAT_00410298)[param_1 & 0x1f];
    param_2 = param_2 + -1;
    param_3 = param_3 + 1;
  } while (param_2 != 0);
  return;
}



/* Address: 00405abc */

undefined4 FUN_00405abc(int *param_1)

{
  int *piVar1;
  uint uVar2;
  DWORD DVar3;
  bool bVar4;
  byte *pbVar5;
  int iVar6;
  DWORD *pDVar7;
  
  if (param_1 != (int *)0x0) {
    iVar6 = *param_1;
    piVar1 = param_1 + 1;
    pDVar7 = (DWORD *)(piVar1 + iVar6 * 2 + 1);
    do {
      pDVar7 = pDVar7 + -2;
      iVar6 = iVar6 + -1;
      uVar2 = piVar1[iVar6 * 2];
      pbVar5 = (byte *)(uVar2 + DAT_0041634c);
      DVar3 = *pDVar7;
      bVar4 = FUN_0040bf30((DWORD)pbVar5,(char *)piVar1,DVar3,(char *)pbVar5);
      if (!bVar4) {
        return 0;
      }
      param_1 = (int *)FUN_00405a90(uVar2,DVar3,pbVar5);
    } while (iVar6 != 0);
  }
  return CONCAT31((int3)((uint)param_1 >> 8),1);
}



/* Address: 00405b28 */

undefined4 FUN_00405b28(int *param_1)

{
  uint uVar1;
  char *pcVar2;
  bool bVar3;
  undefined4 in_EAX;
  char *extraout_ECX;
  char *extraout_EDX;
  int iVar4;
  byte *pbVar5;
  int *local_4;
  
  if (param_1 != (int *)0x0) {
    iVar4 = *param_1;
    local_4 = param_1 + iVar4 * 2 + 2;
    do {
      local_4 = local_4 + -2;
      iVar4 = iVar4 + -1;
      uVar1 = param_1[iVar4 * 2 + 1];
      pbVar5 = (byte *)(uVar1 + DAT_0041634c);
      pcVar2 = (char *)*local_4;
      FUN_00405a90(uVar1,(int)pcVar2,pbVar5);
      bVar3 = FUN_0040bf58(extraout_ECX,extraout_EDX,pcVar2,(char *)pbVar5);
      if (!bVar3) {
        return 0;
      }
      in_EAX = FUN_00405a90(uVar1,(int)pcVar2,pbVar5);
    } while (iVar4 != 0);
  }
  return CONCAT31((int3)((uint)in_EAX >> 8),1);
}



/* Address: 00405ba0 */

void FUN_00405ba0(int *param_1)

{
  uint *puVar1;
  
  if (param_1 != (int *)0x0) {
    puVar1 = (uint *)(param_1 + *param_1 * 2 + 2);
    do {
      puVar1 = puVar1 + -2;
      FUN_00408e4c(*puVar1,(undefined1 *)
                           (*(int *)((int)puVar1 + (4 - (int)(param_1 + 2)) + (int)param_1) +
                           DAT_0041634c));
    } while ((uint *)(param_1 + 2) != puVar1);
  }
  return;
}



/* Address: 00405bdc */

void __fastcall FUN_00405bdc(DWORD param_1,char *param_2)

{
  FUN_0040bf30(param_1,param_2,0x34,&DAT_004162a0);
  return;
}



/* Address: 00405bec */

void __fastcall FUN_00405bec(char *param_1,char *param_2)

{
  char *extraout_ECX;
  char *extraout_EDX;
  
  FUN_0040bf58(param_1,param_2,(char *)0x8,&DAT_004102b8);
  FUN_0040bf58(extraout_ECX,extraout_EDX,(char *)0x34,&DAT_004162a0);
  FUN_00405b28(DAT_004162e4);
  FUN_00405b28(DAT_004162e8);
  return;
}



/* Address: 00405c1c */

void __fastcall FUN_00405c1c(char *param_1,char *param_2)

{
  FUN_0040bf58(param_1,param_2,(char *)0x4,&stack0x00000004);
  return;
}



/* Address: 00405c2c */

void FUN_00405c2c(int param_1,int param_2,byte *param_3)

{
  if (param_2 != 0) {
    do {
      param_2 = param_2 + -1;
      if ((*param_3 & 2) == 0) {
        *(int *)(param_3 + 0xc) = *(int *)(param_3 + 0xc) + param_1;
      }
      param_3 = param_3 + 0x10;
    } while (param_2 != 0);
  }
  return;
}



/* Address: 00405c58 */

undefined4 FUN_00405c58(undefined4 param_1)

{
  DWORD DVar1;
  char *pcVar2;
  char cVar3;
  bool bVar4;
  uint uVar5;
  undefined4 uVar6;
  DWORD extraout_ECX;
  DWORD extraout_ECX_00;
  undefined4 extraout_ECX_01;
  DWORD extraout_ECX_02;
  undefined4 extraout_ECX_03;
  undefined4 extraout_ECX_04;
  char *extraout_EDX;
  char *extraout_EDX_00;
  undefined4 extraout_EDX_01;
  char *extraout_EDX_02;
  undefined4 extraout_EDX_03;
  undefined4 extraout_EDX_04;
  undefined4 uVar7;
  int local_8;
  int local_4;
  
  uVar5 = FUN_0040be34(param_1,&DAT_004162a0,(uint *)&DAT_004162f8);
  cVar3 = (char)uVar5;
  DVar1 = extraout_ECX;
  pcVar2 = extraout_EDX;
  while( true ) {
    if (cVar3 == '\0') {
      return 0;
    }
    if (DAT_00416761 == '\0') break;
    bVar4 = FUN_0040bf30(DVar1,pcVar2,8,(char *)&local_8);
    if (((bVar4) && (local_8 == 0x3d53563d)) && (local_4 == -0x7fffffea)) {
      FUN_00405bdc(extraout_ECX_00,extraout_EDX_00);
      DAT_004162b8._1_1_ = DAT_00416762;
      DAT_004162b8._2_1_ = DAT_00416763;
      DAT_004116a8 = FUN_0040bf80();
      FUN_00405abc(DAT_004162e4);
      FUN_00405abc(DAT_004162e8);
      FUN_00405ba0(DAT_004162e8);
      DAT_00416761 = '\0';
      uVar6 = extraout_ECX_03;
      uVar7 = extraout_EDX_03;
      goto LAB_00405d42;
    }
    FUN_0040bfb8();
    FUN_0040bfcc(extraout_ECX_01,extraout_EDX_01);
    FUN_00405d50(param_1);
    uVar5 = FUN_0040be34(param_1,&DAT_004162a0,(uint *)&DAT_004162f8);
    cVar3 = (char)uVar5;
    DVar1 = extraout_ECX_02;
    pcVar2 = extraout_EDX_02;
  }
  FUN_0040bf94(DAT_004116a8);
  FUN_00405abc(DAT_004162e4);
  FUN_00405abc(DAT_004162e8);
  uVar6 = extraout_ECX_04;
  uVar7 = extraout_EDX_04;
LAB_00405d42:
  uVar6 = FUN_0040bfcc(uVar6,uVar7);
  return CONCAT31((int3)((uint)uVar6 >> 8),1);
}



/* Address: 00405d50 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_00405d50(undefined4 param_1)

{
  uint uVar1;
  undefined4 uVar2;
  char *extraout_ECX;
  undefined4 extraout_ECX_00;
  undefined4 extraout_ECX_01;
  char *extraout_EDX;
  undefined4 extraout_EDX_00;
  undefined4 extraout_EDX_01;
  undefined4 uVar3;
  
  uVar1 = FUN_0040be34(param_1,&DAT_004162a0,(uint *)&DAT_004162f8);
  if ((char)uVar1 != '\0') {
    if (DAT_00416761 == '\0') {
      FUN_0040bf94(DAT_004116a8);
      FUN_00405b28(DAT_004162e4);
      FUN_00405b28(DAT_004162e8);
      uVar2 = extraout_ECX_01;
      uVar3 = extraout_EDX_01;
    }
    else {
      _DAT_004162b0 = 0;
      FUN_00405bec(extraout_ECX,extraout_EDX);
      uVar2 = extraout_ECX_00;
      uVar3 = extraout_EDX_00;
    }
    uVar2 = FUN_0040bfcc(uVar2,uVar3);
    return CONCAT31((int3)((uint)uVar2 >> 8),1);
  }
  return 0;
}



/* Address: 00405db4 */

void FUN_00405db4(undefined4 param_1)

{
  FUN_0040bec0(param_1,&DAT_004102c0,(uint *)&DAT_004162f8);
  return;
}



/* Address: 00405dcc */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_00405dcc(undefined4 param_1)

{
  undefined1 uVar1;
  int iVar2;
  int iVar3;
  bool bVar4;
  uint uVar5;
  uint *puVar6;
  DWORD extraout_ECX;
  undefined4 extraout_ECX_00;
  undefined4 extraout_ECX_01;
  DWORD extraout_ECX_02;
  DWORD extraout_ECX_03;
  undefined4 extraout_ECX_04;
  char *extraout_EDX;
  char *extraout_EDX_00;
  undefined4 extraout_EDX_01;
  char *extraout_EDX_02;
  char *extraout_EDX_03;
  char *extraout_EDX_04;
  char *extraout_EDX_05;
  char *extraout_EDX_06;
  char *extraout_EDX_07;
  char *pcVar7;
  undefined4 extraout_EDX_08;
  uint uVar8;
  byte *pbVar9;
  int iVar10;
  int local_14;
  int local_10;
  undefined1 local_c;
  undefined1 local_b;
  int local_8;
  uint *local_4;
  
  uVar5 = FUN_0040be34(param_1,&DAT_004102c0,(uint *)&DAT_004162f8);
  if ((char)uVar5 != '\0') {
    bVar4 = FUN_0040bf30(extraout_ECX,extraout_EDX,8,(char *)&local_14);
    uVar1 = DAT_004162b4;
    if (((!bVar4) || (local_14 != 0x3d53563d)) || (local_10 != -0x7fffffea)) {
      FUN_0040bfb8();
      FUN_0040bfcc(extraout_ECX_01,extraout_EDX_01);
      return 0;
    }
    local_c = DAT_004162b6;
    local_b = DAT_004162b7;
    FUN_00405bdc(CONCAT31((int3)((uint)extraout_ECX_00 >> 8),DAT_004162b7),extraout_EDX_00);
    DAT_004162b6 = local_c;
    DAT_004162b7 = local_b;
    DAT_004162b8._1_1_ = DAT_00416762;
    DAT_004162b8._2_1_ = 0;
    DAT_004162b4 = uVar1;
    FUN_0040a240(0);
    iVar3 = DAT_004162c4;
    iVar2 = DAT_004162c0;
    DAT_00416761 = 1;
    DAT_0041675f = 1;
    DAT_00416770 = 0;
    FUN_00405514(DAT_004162a8);
    FUN_00405abc(DAT_004162e4);
    FUN_00405abc(DAT_004162e8);
    FUN_00405abc(DAT_004162ec);
    uVar5 = 0;
    pcVar7 = extraout_EDX_02;
    do {
      FUN_0040bf30((DWORD)&local_8,pcVar7,4,(char *)&local_8);
      iVar10 = local_8 * 0x10 + _DAT_004161e8;
      FUN_0040bf30((DWORD)&local_8,extraout_EDX_03,4,(char *)&local_8);
      *(int *)(iVar10 + 0xc) = local_8;
      uVar8 = local_8 * 0x10 + DAT_004161e0 * 2;
      local_4 = FUN_00404594(uVar8);
      *(uint **)(iVar10 + 8) = local_4;
      FUN_0040bf30(extraout_ECX_02,extraout_EDX_04,uVar8,(char *)local_4);
      pbVar9 = (byte *)((int)local_4 + DAT_004161e0 * 2);
      FUN_00405c2c(DAT_0041628c,local_8,pbVar9);
      pcVar7 = extraout_EDX_05;
      while (bVar4 = local_8 != 0, local_8 = local_8 + -1, bVar4) {
        if (((*pbVar9 & 2) != 0) && ((*pbVar9 & 8) != 0)) {
          uVar8 = (uint)*(byte *)((uint)pbVar9[1] * 0x10 + 3 + DAT_004161c8);
          puVar6 = FUN_00404594(uVar8);
          *(uint **)(pbVar9 + 0xc) = puVar6;
          FUN_0040bf30(extraout_ECX_03,extraout_EDX_06,uVar8,(char *)puVar6);
          pcVar7 = extraout_EDX_07;
        }
        pbVar9 = pbVar9 + 0x10;
      }
      uVar5 = uVar5 + 1;
    } while (uVar5 < DAT_004162f0);
    DAT_00416761 = 0;
    DAT_0041675f = 0;
    FUN_004058f8('\0',DAT_004162ac);
    FUN_0040bfcc(extraout_ECX_04,extraout_EDX_08);
    *(undefined1 *)(DAT_004162e0 + 1) = DAT_004162b8._3_1_;
    *(undefined4 *)(DAT_004162e0 + 4) = DAT_004162bc;
    if (-1 < iVar2) {
      FUN_00406cd8(iVar2);
    }
    DAT_004162b8._2_1_ = 0;
    if (-1 < iVar3) {
      FUN_00403484('\0',iVar3);
    }
    DAT_00416770 = 1;
    FUN_004039d8();
  }
  return 0;
}



/* Address: 00406008 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_00406008(undefined4 param_1)

{
  char *pcVar1;
  uint uVar2;
  undefined4 uVar3;
  undefined4 extraout_ECX;
  int extraout_ECX_00;
  char *extraout_ECX_01;
  char *pcVar4;
  char *extraout_EDX;
  undefined4 extraout_EDX_00;
  char *extraout_EDX_01;
  undefined4 extraout_EDX_02;
  undefined4 extraout_EDX_03;
  byte *pbVar5;
  int *piVar6;
  int iVar7;
  int local_4;
  
  uVar2 = FUN_0040be34(param_1,&DAT_004102c0,(uint *)&DAT_004162f8);
  if ((char)uVar2 == '\0') {
    return 0;
  }
  DAT_00416770 = 0;
  FUN_00404c64(DAT_004162d8);
  DAT_004162b8._3_1_ = *(undefined1 *)(DAT_004162e0 + 1);
  DAT_004162bc = *(undefined4 *)(DAT_004162e0 + 4);
  _DAT_004162b0 = DAT_004162f0;
  DAT_004162a8 = DAT_004162d4;
  DAT_004162ac = DAT_004162d8;
  FUN_00405bec((char *)CONCAT31((int3)((uint)extraout_ECX >> 8),DAT_004162b8._3_1_),extraout_EDX);
  FUN_00405b28(DAT_004162ec);
  uVar2 = 0;
  local_4 = 0;
  iVar7 = extraout_ECX_00;
  uVar3 = extraout_EDX_00;
  do {
    piVar6 = (int *)(local_4 + _DAT_004161e8);
    if (*piVar6 != 0) {
      iVar7 = piVar6[3];
      pcVar1 = (char *)piVar6[2];
      pbVar5 = (byte *)(pcVar1 + (int)DAT_004161e0 * 2);
      FUN_00405c1c(DAT_004161e0,pcVar1);
      FUN_00405c1c(extraout_ECX_01,extraout_EDX_01);
      FUN_00405c2c(-DAT_0041628c,iVar7,pbVar5);
      pcVar4 = (char *)(iVar7 * 0x10) + (int)DAT_004161e0 * 2;
      FUN_0040bf58(pcVar4,(char *)(iVar7 * 0x10),pcVar4,pcVar1);
      FUN_00405c2c(DAT_0041628c,iVar7,pbVar5);
      uVar3 = extraout_EDX_02;
      while (iVar7 != 0) {
        iVar7 = iVar7 + -1;
        if (((*pbVar5 & 2) != 0) && ((*pbVar5 & 8) != 0)) {
          pcVar4 = (char *)(uint)(byte)((char *)((uint)pbVar5[1] * 0x10))[DAT_004161c8 + 3];
          FUN_0040bf58(pcVar4,(char *)((uint)pbVar5[1] * 0x10),pcVar4,*(char **)(pbVar5 + 0xc));
          uVar3 = extraout_EDX_03;
        }
        pbVar5 = pbVar5 + 0x10;
      }
    }
    local_4 = local_4 + 0x10;
    uVar2 = uVar2 + 1;
  } while (uVar2 < DAT_004161e4);
  FUN_0040bfcc(iVar7,uVar3);
  uVar3 = FUN_004058f8('\x01',DAT_004162d8);
  DAT_00416770 = 1;
  return CONCAT31((int3)((uint)uVar3 >> 8),1);
}



/* Address: 00406170 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00406170(LONG param_1)

{
  DAT_004116b8 = 1;
  DAT_004116f4 = 0;
  DAT_004116f0 = 0;
  DAT_004116e4 = 0;
  DAT_004116dc = 0;
  DAT_004116d4 = 0;
  DAT_004116cc = 0;
  _DAT_004116c4 = 0;
  DAT_004116c0 = 0;
  DAT_004116bc = 0;
  DAT_004116e8 = 0;
  DAT_004116e0 = 0;
  DAT_004116d8 = 0;
  DAT_004116d0 = 0;
  DAT_004116c8 = 0;
  DAT_004116ed = 0;
  DAT_004116ec = 0;
  DAT_004116f8 = DAT_004161fc;
  DAT_004116fc = DAT_00416200;
  FUN_00406958();
  FUN_0040bcf8(0,param_1);
  return;
}



/* Address: 004061f4 */

void FUN_004061f4(int param_1)

{
  if (param_1 != 0) {
    FUN_0040461c(param_1);
  }
  return;
}



/* Address: 00406208 */

undefined4 FUN_00406208(void)

{
  undefined4 uVar1;
  
  if (DAT_004116ec != '\0') {
    FUN_0040a268();
  }
  FUN_00406958();
  FUN_004061f4(DAT_004116c8);
  FUN_004061f4(DAT_004116d0);
  FUN_004061f4(DAT_004116d8);
  FUN_004061f4(DAT_004116e0);
  FUN_004061f4(DAT_004116e8);
  uVar1 = FUN_004043f0();
  return CONCAT31((int3)((uint)uVar1 >> 8),1);
}



/* Address: 0040625c */

void FUN_0040625c(void)

{
  FUN_00406208();
  FUN_0040bca8(0x4102c4);
  return;
}



/* Address: 0040626c */

bool __fastcall FUN_0040626c(DWORD param_1,char *param_2,undefined4 param_3,byte *param_4)

{
  bool bVar1;
  
  if (DAT_004116b0 == 0) {
    return true;
  }
  if (DAT_004116b0 != DAT_004116b4) {
    FUN_0040bd20(param_1,param_2,DAT_004116b0,(char *)DAT_004116c8);
    FUN_00409630(DAT_004116b0,param_4,DAT_004116c8);
    return true;
  }
  bVar1 = FUN_0040bd20(param_1,param_2,DAT_004116b0,(char *)param_4);
  return bVar1;
}



/* Address: 004062c0 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_004062c0(void)

{
  uint *in_EAX;
  undefined4 uVar1;
  
  switch(DAT_004116ad) {
  case 0:
    if ((DAT_004116b0 == (uint *)0x563d2d5b) && (DAT_004116b4 == (uint *)0x5d2d3d53)) {
      return CONCAT31((int3)((uint)in_EAX >> 8),3);
    }
LAB_00406401:
    uVar1 = FUN_0040625c();
    return uVar1;
  case 1:
    DAT_004116b8 = (uint *)0x1;
    DAT_004116bc = 0;
    if (DAT_004116ae != '\0') {
      DAT_004116b8 = DAT_004116b0;
      in_EAX = DAT_004116b0;
    }
    if (DAT_004116af != '\0') {
      DAT_004116c0 = DAT_004116b4;
      in_EAX = DAT_004116b4;
    }
    break;
  case 2:
    if (DAT_004116ae != '\0') {
      _DAT_004116c4 = DAT_004116b0;
      in_EAX = FUN_00404594((uint)DAT_004116b0);
      DAT_004116c8 = in_EAX;
      if (in_EAX == (uint *)0x0) {
        DAT_004116c8 = (uint *)0x0;
        goto LAB_00406401;
      }
    }
    break;
  case 3:
    if (DAT_004116ae != '\0') {
      DAT_004116cc = DAT_004116b0;
      in_EAX = FUN_00404594((uint)DAT_004116b0);
      DAT_004116d0 = in_EAX;
      if (in_EAX == (uint *)0x0) goto LAB_00406401;
    }
    if (DAT_004116af != '\0') {
      DAT_004116d4 = DAT_004116b4;
      in_EAX = FUN_00404594((uint)DAT_004116b4);
      DAT_004116d8 = in_EAX;
      if (in_EAX == (uint *)0x0) {
        DAT_004116d8 = (uint *)0x0;
        goto LAB_00406401;
      }
    }
    break;
  case 4:
    if (DAT_004116ae != '\0') {
      DAT_004116dc = DAT_004116b0;
      in_EAX = FUN_00404594((uint)DAT_004116b0);
      DAT_004116e0 = in_EAX;
      if (in_EAX == (uint *)0x0) goto LAB_00406401;
    }
    if (DAT_004116af != '\0') {
      DAT_004116e4 = DAT_004116b4;
      in_EAX = FUN_00404594((uint)DAT_004116b4);
      DAT_004116e8 = in_EAX;
      if (in_EAX == (uint *)0x0) goto LAB_00406401;
    }
    break;
  case 5:
    if (DAT_004116ae != '\0') {
      DAT_004116f0 = DAT_004116b0;
      in_EAX = DAT_004116b0;
    }
    if (DAT_004116af != '\0') {
      DAT_004116f0 = DAT_004116b4;
      in_EAX = DAT_004116b4;
    }
    break;
  case 6:
    if (DAT_004116ae != '\0') {
      DAT_004116f8 = DAT_004116b0;
      in_EAX = DAT_004116b0;
    }
    if (DAT_004116af != '\0') {
      DAT_004116fc = DAT_004116b4;
      in_EAX = DAT_004116b4;
    }
  }
  return CONCAT31((int3)((uint)in_EAX >> 8),1);
}



/* Address: 0040645c */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 __fastcall FUN_0040645c(DWORD param_1,char *param_2)

{
  byte bVar1;
  bool bVar2;
  char cVar3;
  int iVar4;
  uint uVar5;
  undefined4 uVar6;
  byte *pbVar7;
  byte *pbVar8;
  int iVar9;
  int iVar10;
  DWORD local_8;
  uint local_4;
  
  bVar2 = FUN_0040626c(param_1,param_2,DAT_004116cc,DAT_004116d0);
  pbVar8 = DAT_004116d0;
  if (!bVar2) {
    return 0;
  }
  local_8 = 1;
  if (DAT_004116ad == '\x01') {
    DAT_004116ee = 0;
    FUN_0040a320();
    FUN_00409480((char)_DAT_004116af);
    if (1 < DAT_004116b8) {
      DAT_004116bc = FUN_0040bce4();
    }
    local_8 = 0;
    DAT_00411700 = '\0';
  }
  else if (DAT_004116ad == '\x02') {
    DAT_004116ee = 1;
    DAT_004116b8 = DAT_004116b8 - 1;
    if (DAT_004116b8 != 0) {
      FUN_0040bcf8(0,DAT_004116bc);
    }
  }
  if (CONCAT13(DAT_004116b0._3_1_,(undefined3)DAT_004116b0) != 0) {
    do {
      bVar1 = *pbVar8;
      if ((bVar1 & 0x40) == 0) {
        iVar9 = DAT_004116f0 + (uint)pbVar8[1];
        pbVar7 = pbVar8 + 2;
        if ((bVar1 & 4) != 0) {
          pbVar7 = pbVar8 + 3;
          iVar9 = iVar9 + (uint)pbVar8[2] * 0x100;
        }
        iVar10 = DAT_004116f4 + (uint)*pbVar7;
        pbVar8 = pbVar7 + 1;
        if ((bVar1 & 8) != 0) {
          pbVar8 = pbVar7 + 2;
          iVar10 = iVar10 + (uint)pbVar7[1] * 0x100;
        }
        uVar5 = (uint)*pbVar8;
        pbVar7 = pbVar8 + 1;
        if ((bVar1 & 0x10) != 0) {
          pbVar7 = pbVar8 + 2;
          uVar5 = uVar5 + (uint)pbVar8[1] * 0x100;
        }
        local_4 = (uint)*pbVar7;
        pbVar8 = pbVar7 + 1;
        if ((bVar1 & 0x20) != 0) {
          pbVar8 = pbVar7 + 2;
          local_4 = local_4 + (uint)pbVar7[1] * 0x100;
        }
      }
      else {
        local_4 = DAT_004116fc;
        pbVar8 = pbVar8 + 1;
        uVar5 = DAT_004116f8;
        iVar9 = DAT_004116f0;
        iVar10 = DAT_004116f4;
      }
      iVar4 = FUN_004095ec();
      pbVar8 = pbVar8 + iVar4;
      if (DAT_00411700 != '\0') {
        FUN_0040968c('\0',iVar10 + local_4,iVar9 + uVar5,iVar10,iVar9);
      }
    } while ((bVar1 & 0x80) == 0);
  }
  if (DAT_00411700 != '\0') {
    FUN_00403948('\x01');
  }
  cVar3 = FUN_0040bb60(&local_8);
  if (cVar3 == -0x7e) {
    return 2;
  }
  if (cVar3 == -0x7d) {
    return 3;
  }
  if (DAT_004116ad == '\x01') {
    FUN_0040968c((char)_DAT_00416216,DAT_004116f4 + DAT_004116fc,DAT_004116f0 + DAT_004116f8,
                 DAT_004116f4,DAT_004116f0);
    FUN_0040bb60((DWORD *)&DAT_00411704);
    DAT_00411708 = 0;
    DAT_0041170c = '\0';
    DAT_00411700 = 1;
  }
  else if (DAT_004116c0 == 0) {
    DAT_00411700 = 1;
  }
  else {
    uVar5 = (local_8 - _DAT_00411704) / DAT_00411708;
    if (DAT_004116c0 < uVar5) {
      if ((DAT_004116dc != 0) && (DAT_0041170c = DAT_0041170c + '\x01', DAT_0041170c != '\b')) {
        DAT_00411700 = 0;
        goto LAB_00406726;
      }
    }
    else if (uVar5 < DAT_004116c0) {
      do {
        cVar3 = FUN_0040bb60(&local_8);
        if (cVar3 == -0x7e) {
          return 2;
        }
        if (cVar3 == -0x7d) {
          return 3;
        }
      } while ((local_8 - _DAT_00411704) / DAT_00411708 < DAT_004116c0);
    }
    DAT_0041170c = '\0';
    DAT_00411700 = 1;
  }
LAB_00406726:
  uVar6 = FUN_0040bb58();
  DAT_00411708 = DAT_00411708 + 1;
  return CONCAT31((int3)((uint)uVar6 >> 8),1);
}



/* Address: 00406748 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

bool __fastcall FUN_00406748(DWORD param_1,char *param_2)

{
  bool bVar1;
  
  bVar1 = FUN_0040626c(param_1,param_2,DAT_004116d4,DAT_004116d8);
  if (bVar1) {
    bVar1 = FUN_0040b780('\0',(char)_DAT_00416216,0x100,DAT_004116d8);
  }
  else {
    bVar1 = false;
  }
  return bVar1;
}



/* Address: 00406780 */

void __fastcall FUN_00406780(DWORD param_1,char *param_2)

{
  bool bVar1;
  undefined4 uVar2;
  DWORD extraout_ECX;
  char *extraout_EDX;
  
  if (DAT_004116ad != '\0') {
    FUN_0040a318();
    param_1 = extraout_ECX;
    param_2 = extraout_EDX;
  }
  bVar1 = FUN_0040626c(param_1,param_2,DAT_004116dc,(byte *)DAT_004116e0);
  if (bVar1) {
    uVar2 = FUN_0040a198(0x81,0,0xff,DAT_004116e0);
    DAT_004116ec = (undefined1)uVar2;
  }
  else {
    DAT_004116ec = 0;
  }
  return;
}



/* Address: 004067c4 */

bool __fastcall FUN_004067c4(DWORD param_1,char *param_2)

{
  bool bVar1;
  DWORD extraout_ECX;
  char *extraout_EDX;
  
  if ((DAT_004116ed != '\0') && ((DAT_004116ee == '\0' || (DAT_004116ad != '\0')))) {
    FUN_00406958();
    DAT_004116ed = '\0';
    param_1 = extraout_ECX;
    param_2 = extraout_EDX;
  }
  if (DAT_004116b0 == 0) {
    return true;
  }
  if (DAT_004116ed != '\0') {
    bVar1 = FUN_0040bcf8(1,DAT_004116b0);
    return bVar1;
  }
  bVar1 = FUN_0040626c(param_1,param_2,DAT_004116e4,DAT_004116e8);
  if (bVar1) {
    DAT_004116ed = FUN_00406940(DAT_004116e8);
  }
  else {
    DAT_004116ed = 0;
  }
  return (bool)DAT_004116ed;
}



/* Address: 0040683c */

void FUN_0040683c(LONG param_1)

{
  char cVar1;
  bool bVar2;
  undefined4 uVar3;
  DWORD extraout_ECX;
  DWORD extraout_ECX_00;
  DWORD extraout_ECX_01;
  DWORD extraout_ECX_02;
  DWORD extraout_ECX_03;
  DWORD extraout_ECX_04;
  DWORD extraout_ECX_05;
  DWORD DVar4;
  DWORD extraout_ECX_06;
  DWORD extraout_ECX_07;
  char *extraout_EDX;
  char *extraout_EDX_00;
  char *extraout_EDX_01;
  char *extraout_EDX_02;
  char *extraout_EDX_03;
  char *extraout_EDX_04;
  char *extraout_EDX_05;
  char *pcVar5;
  char *extraout_EDX_06;
  char *extraout_EDX_07;
  char local_4;
  
  cVar1 = FUN_00406170(param_1);
  DVar4 = extraout_ECX;
  pcVar5 = extraout_EDX;
  if (cVar1 == '\0') {
LAB_0040690e:
    FUN_0040625c();
    return;
  }
LAB_0040684f:
  local_4 = '\0';
  bVar2 = FUN_0040bd20(DVar4,pcVar5,0xc,&DAT_004116ac);
  DVar4 = extraout_ECX_00;
  pcVar5 = extraout_EDX_00;
  if (!bVar2) goto switchD_00406871_default;
  switch(DAT_004116ac) {
  case '\0':
    break;
  case '\x01':
    uVar3 = FUN_0040645c(extraout_ECX_00,extraout_EDX_00);
    local_4 = (char)uVar3;
    DVar4 = extraout_ECX_02;
    pcVar5 = extraout_EDX_02;
    goto switchD_00406871_default;
  case '\x02':
    local_4 = FUN_00406748(extraout_ECX_00,extraout_EDX_00);
    DVar4 = extraout_ECX_03;
    pcVar5 = extraout_EDX_03;
    goto switchD_00406871_default;
  case '\x03':
    local_4 = FUN_00406780(extraout_ECX_00,extraout_EDX_00);
    DVar4 = extraout_ECX_04;
    pcVar5 = extraout_EDX_04;
    goto switchD_00406871_default;
  case '\x04':
    local_4 = FUN_004067c4(extraout_ECX_00,extraout_EDX_00);
    DVar4 = extraout_ECX_05;
    pcVar5 = extraout_EDX_05;
  default:
    goto switchD_00406871_default;
  }
switchD_00406871_caseD_0:
  uVar3 = FUN_004062c0();
  local_4 = (char)uVar3;
  DVar4 = extraout_ECX_01;
  pcVar5 = extraout_EDX_01;
switchD_00406871_default:
  if (local_4 == '\x02') {
    do {
      while( true ) {
        bVar2 = FUN_0040bd20(DVar4,pcVar5,0xc,&DAT_004116ac);
        if (!bVar2) goto LAB_0040690e;
        if (DAT_004116ac == '\0') break;
        bVar2 = FUN_0040bcf8(1,DAT_004116b0);
        DVar4 = extraout_ECX_07;
        pcVar5 = extraout_EDX_07;
        if (!bVar2) goto LAB_0040690e;
      }
    } while ((DAT_004116ad != '\0') &&
            (DVar4 = extraout_ECX_06, pcVar5 = extraout_EDX_06, DAT_004116ad != '\x01'));
    goto switchD_00406871_caseD_0;
  }
  if (local_4 == '\0') goto LAB_0040690e;
  if (local_4 == '\x03') {
    FUN_00406208();
    return;
  }
  goto LAB_0040684f;
}



/* Address: 0040691c */

int FUN_0040691c(void)

{
  int iVar1;
  
  if ((DAT_0041620c == 0) || (DAT_00416217 != '\x01')) {
    iVar1 = 1;
  }
  else {
    iVar1 = FUN_00406d48(0);
  }
  return iVar1;
}



/* Address: 00406940 */

void FUN_00406940(undefined4 param_1)

{
  DAT_00411710 = FUN_0040a188(0,0xffffffff,param_1);
  return;
}



/* Address: 00406958 */

void FUN_00406958(void)

{
  if (DAT_00411710 != '\0') {
    FUN_0040a260();
  }
  DAT_004162c0 = 0xffffffff;
  DAT_00411710 = 0;
  return;
}



/* Address: 00406a50 */

bool FUN_00406a50(uint param_1,uint param_2)

{
  uint uVar1;
  
  uVar1 = DAT_00416354;
  if (3 < DAT_00416354) {
    uVar1 = DAT_00416354 - 4;
    param_1 = (uint)(byte)(&DAT_004103f0)[param_1];
  }
  return (param_1 + uVar1 * 2 & 7) == param_2;
}



/* Address: 00406aec */

void FUN_00406aec(undefined4 *param_1)

{
  undefined4 uVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined4 uVar6;
  undefined4 uVar7;
  undefined4 uVar8;
  undefined4 uVar9;
  undefined4 uVar10;
  undefined4 uVar11;
  undefined4 uVar12;
  undefined1 uVar13;
  undefined1 uVar14;
  
  uVar14 = DAT_00416764;
  uVar13 = DAT_0041675e;
  uVar12 = DAT_00416350;
  uVar11 = DAT_004161bc;
  uVar10 = DAT_004161b8;
  uVar9 = DAT_004161b4;
  uVar8 = DAT_004161b0;
  uVar7 = DAT_004161ac;
  uVar6 = DAT_004161a8;
  uVar5 = DAT_004161a4;
  uVar4 = DAT_004161a0;
  uVar3 = DAT_0041619c;
  uVar2 = DAT_00416198;
  uVar1 = DAT_00416194;
  FUN_00403a64('\x01',(uint *)*param_1);
  DAT_00416764 = uVar14;
  DAT_00416350 = uVar12;
  DAT_004161b4 = uVar9;
  DAT_004161b0 = uVar8;
  DAT_0041675e = uVar13;
  DAT_004161a4 = uVar5;
  DAT_004161a0 = uVar4;
  DAT_004161ac = uVar7;
  DAT_004161a8 = uVar6;
  DAT_004161bc = uVar11;
  DAT_004161b8 = uVar10;
  DAT_00416194 = uVar1;
  DAT_0041619c = uVar3;
  DAT_00416198 = uVar2;
  return;
}



/* Address: 00406cd8 */

int FUN_00406cd8(int param_1)

{
  char cVar1;
  
  if (DAT_00416760 == '\0') {
    FUN_00406958();
    if (DAT_004162b5 != '\0') {
      DAT_004162c0 = param_1;
      cVar1 = FUN_00406940(*(undefined4 *)(DAT_0041629c + param_1 * 4));
      return (int)cVar1;
    }
  }
  return 1;
}



/* Address: 00406d48 */

int FUN_00406d48(int param_1)

{
  char cVar1;
  
  if (DAT_004162b7 != '\0') {
    DAT_00416769 = DAT_00416769 + '\x01';
    cVar1 = FUN_0040683c(*(LONG *)(DAT_00416338 + param_1 * 4));
    DAT_00416769 = DAT_00416769 + -1;
    return (int)cVar1;
  }
  return 1;
}



/* Address: 00406d80 */

undefined4 FUN_00406d80(char param_1,undefined2 param_2,undefined2 param_3,int param_4)

{
  int iVar1;
  byte *pbVar2;
  int iVar3;
  undefined2 uVar4;
  byte *pbVar5;
  uint uVar6;
  uint uVar7;
  
  iVar1 = DAT_004161dc + param_4 * 8;
  pbVar2 = FUN_00404760();
  *pbVar2 = *pbVar2 | 0x80;
  if ((*(byte *)(iVar1 + 1) & 1) != 0) {
    *pbVar2 = *pbVar2 | 4;
  }
  pbVar2[2] = *(byte *)(iVar1 + 3);
  uVar4 = 0xffff;
  if (param_1 == '\0') {
    uVar4 = (undefined2)DAT_0041619c;
  }
  *(undefined2 *)(pbVar2 + 6) = uVar4;
  *(undefined2 *)(pbVar2 + 8) = param_3;
  *(undefined2 *)(pbVar2 + 10) = param_2;
  if (param_1 == '\0') {
    if (DAT_00416198 == (byte *)0x0) {
      pbVar2[3] = *(byte *)(DAT_00416194 + 2);
    }
    else {
      iVar3 = DAT_004161c0 - (int)pbVar2;
      uVar7 = (uint)*(ushort *)(DAT_00416198 + 8);
      uVar6 = (uint)*(ushort *)(DAT_00416198 + 10);
      if (uVar6 != 0xffff) {
        pbVar5 = (byte *)(DAT_004161c0 + uVar6 * -0x10);
        FUN_00408b04(pbVar5);
        *pbVar5 = *pbVar5 & 0x7f;
        if (uVar6 != uVar7) {
          FUN_004047a4(pbVar5);
        }
      }
      uVar4 = (undefined2)(iVar3 / 0x10);
      *(undefined2 *)(DAT_00416198 + 10) = uVar4;
      if ((*pbVar2 & 4) == 0) {
        if (uVar7 != 0xffff) {
          FUN_004047a4((byte *)(DAT_004161c0 + uVar7 * -0x10));
        }
        *(undefined2 *)(DAT_00416198 + 8) = uVar4;
      }
      pbVar2[3] = DAT_00416198[5];
      if ((DAT_004161a0 != DAT_004161a8) || (DAT_004161a4 != DAT_004161ac)) {
        *DAT_00416198 = *DAT_00416198 | 4;
      }
    }
  }
  else {
    pbVar2[5] = 0xff;
    pbVar2[4] = 0xff;
    pbVar2[3] = DAT_00416198[5];
  }
  FUN_00408b60(param_1,pbVar2,iVar1);
  return 1;
}



/* Address: 00406f7c */

byte * FUN_00406f7c(undefined2 param_1,int *param_2,int param_3,int param_4)

{
  int iVar1;
  undefined4 *puVar2;
  byte *pbVar3;
  
  iVar1 = DAT_004161dc + param_3 * 8;
  pbVar3 = FUN_00404760();
  *pbVar3 = *pbVar3 | 0xe0;
  pbVar3[1] = 0;
  pbVar3[2] = 1;
  pbVar3[3] = *(byte *)(DAT_00416198 + 5);
  pbVar3[5] = 0xff;
  pbVar3[4] = 0xff;
  *(undefined2 *)(pbVar3 + 6) = (undefined2)DAT_0041619c;
  *(short *)(pbVar3 + 8) = (short)*param_2;
  *(undefined2 *)(pbVar3 + 10) = param_1;
  puVar2 = (undefined4 *)(*(int *)(iVar1 + 4) + (param_4 - (uint)*(byte *)(iVar1 + 1)) * 8);
  *(undefined4 **)(pbVar3 + 0xc) = puVar2;
  *param_2 = *param_2 + ((uint)*(ushort *)*puVar2 - (int)*(short *)(puVar2 + 1));
  FUN_00408b04(pbVar3);
  return pbVar3;
}



/* Address: 00407000 */

undefined4 FUN_00407000(undefined4 param_1)

{
  FUN_004076d8();
  *(byte *)(DAT_00416198 + 3) = *(byte *)(DAT_00416198 + 3) | 2;
  return param_1;
}



/* Address: 00407018 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00407018(int param_1)

{
  undefined4 *puVar1;
  
  do {
    DAT_00411c24 = DAT_00411c24 + -1;
    puVar1 = *(undefined4 **)(*(byte **)(&DAT_00411828 + DAT_00411c24 * 4) + 0xc);
    _DAT_0041171c = _DAT_0041171c - ((uint)*(ushort *)*puVar1 - (int)*(short *)(puVar1 + 1));
    FUN_004023bc(*(byte **)(&DAT_00411828 + DAT_00411c24 * 4));
    param_1 = param_1 + -1;
  } while (param_1 != 0);
  return;
}



/* Address: 00407058 */

undefined4 FUN_00407058(byte param_1)

{
  uint *puVar1;
  uint *puVar2;
  byte *pbVar3;
  undefined4 uVar4;
  uint uVar5;
  int iVar6;
  byte *pbVar7;
  
  DAT_00416198 = DAT_00411c34;
  DAT_0041619c = DAT_00411c3c;
  DAT_00416194 = DAT_00411c38;
LAB_0040707b:
  puVar1 = DAT_00411714;
  if (param_1 == 8) {
    if (DAT_00411c31 != '\0') {
      pbVar7 = (byte *)0x0;
      if (DAT_00411c24 != (byte *)0x0) {
        pbVar7 = (byte *)FUN_00407018(1);
      }
      goto LAB_0040735e;
    }
  }
  else {
    if (param_1 == 0x1b) {
      if (DAT_00411c31 != '\0') {
        if (DAT_00411c24 != (byte *)0x0) {
          uVar4 = FUN_00407018((int)DAT_00411c24);
          return CONCAT31((int3)((uint)uVar4 >> 8),1);
        }
        goto LAB_004070f1;
      }
      goto LAB_004072a8;
    }
    if (param_1 == 0) {
LAB_004070f1:
      if (DAT_0041676a != '\0') {
        DAT_0041676a = 0;
        uVar4 = FUN_004076d8();
        return uVar4;
      }
      DAT_0041676a = '\x01';
      DAT_00411c31 = '\0';
      param_1 = (byte)*DAT_00411714;
      DAT_00411714 = (uint *)((int)DAT_00411714 + 1);
      goto LAB_0040707b;
    }
    if (param_1 == 0xd) {
      if (DAT_00411c31 == '\0') goto LAB_004072a8;
      DAT_00411c24[0x411728] = 0;
      switch(DAT_00411c30 & 7) {
      case 0:
        iVar6 = FUN_0040c58c(&DAT_00411728);
        *(char *)DAT_00411c2c = (char)iVar6;
        break;
      case 1:
        FUN_0040c5d8((uint *)*DAT_00411c2c,(uint *)&DAT_00411728);
        break;
      case 2:
        FUN_0040c5d8(DAT_00411c2c,(uint *)&DAT_00411728);
        break;
      case 3:
        uVar5 = FUN_0040c58c(&DAT_00411728);
        *DAT_00411c2c = uVar5;
        break;
      case 4:
        iVar6 = FUN_0040c58c(&DAT_00411728);
        *(int *)*DAT_00411c2c = iVar6;
      }
      DAT_00411c31 = '\0';
      param_1 = (byte)*DAT_00411714;
      DAT_00411714 = (uint *)((int)DAT_00411714 + 1);
      goto LAB_0040707b;
    }
    if (param_1 == 0xf) {
      DAT_00411c30 = (byte)*DAT_00411714;
      DAT_00411714 = (uint *)((int)DAT_00411714 + 1);
      DAT_00411c28 = (byte *)(uint)*(byte *)DAT_00411714;
      DAT_00411714 = (uint *)((int)puVar1 + 2);
      puVar2 = DAT_00411714;
      if ((DAT_00411c30 & 0x70) == 0) {
        DAT_00411c2c = (uint *)DAT_0041634c;
LAB_00407236:
        if ((DAT_00411c30 & 0x80) == 0) {
          DAT_00411714 = (uint *)((int)puVar1 + 3);
          uVar5 = (uint)*(byte *)puVar2;
        }
        else {
          uVar5 = *DAT_00411714;
          DAT_00411714 = (uint *)((int)puVar1 + 6);
        }
        DAT_00411c2c = (uint *)((int)DAT_00411c2c + uVar5);
        switch(DAT_00411c30 & 7) {
        case 0:
        case 3:
        case 4:
          DAT_00411c32 = '\x01';
          break;
        case 1:
        case 2:
          DAT_00411c32 = '\0';
        }
        DAT_00411c24 = (byte *)0x0;
        DAT_00411c31 = 1;
        return CONCAT31((int3)(uVar5 >> 8),1);
      }
      if ((DAT_00411c30 & 0x70) == 0x10) {
        DAT_00411c2c = (uint *)DAT_00416350;
        goto LAB_00407236;
      }
    }
  }
LAB_004072a8:
  if (DAT_00411c31 != '\0') {
    pbVar7 = DAT_00411c24;
    if (DAT_00411c24 < DAT_00411c28) {
      if (param_1 < DAT_00411724[1]) {
        iVar6 = FUN_0040c6f0((uint)param_1);
        param_1 = (byte)iVar6;
      }
      if (DAT_00411724[2] < param_1) {
        iVar6 = FUN_0040c708((uint)param_1);
        param_1 = (byte)iVar6;
      }
      pbVar7 = DAT_00411724;
      if (((DAT_00411724[1] <= param_1) &&
          (pbVar7 = (byte *)CONCAT31((int3)((uint)DAT_00411724 >> 8),DAT_00411724[2]),
          param_1 <= DAT_00411724[2])) &&
         ((DAT_00411c32 == '\0' || (pbVar7 = (byte *)(uint)param_1, (pbVar7[0x4109f9] & 4) != 0))))
      {
        DAT_00411c24[0x411728] = param_1;
        pbVar7 = FUN_00406f7c((short)DAT_00411720,(int *)&DAT_0041171c,DAT_00411718,(uint)param_1);
        pbVar3 = DAT_00411c24;
        DAT_00411c24 = DAT_00411c24 + 1;
        *(byte **)(&DAT_00411828 + (int)pbVar3 * 4) = pbVar7;
      }
    }
LAB_0040735e:
    return CONCAT31((int3)((uint)pbVar7 >> 8),1);
  }
  FUN_00406f7c((short)DAT_00411720,(int *)&DAT_0041171c,DAT_00411718,(uint)param_1);
  param_1 = (byte)*DAT_00411714;
  DAT_00411714 = (uint *)((int)DAT_00411714 + 1);
  goto LAB_0040707b;
}



/* Address: 00407394 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

int FUN_00407394(undefined4 param_1,undefined4 param_2,int param_3,undefined4 param_4)

{
  undefined4 uVar1;
  
  if (DAT_0041676a == '\0') {
    DAT_00411714 = FUN_00407000(param_4);
    _DAT_0041171c = param_2;
    DAT_00411720 = param_1;
    DAT_00411718 = param_3;
    DAT_00411724 = DAT_004161dc + param_3 * 8;
    DAT_00411c34 = DAT_00416198;
    DAT_00411c3c = DAT_0041619c;
    DAT_00411c38 = DAT_00416194;
    uVar1 = FUN_00407058(0);
    return (int)(char)uVar1;
  }
  return 0;
}



/* Address: 0040745c */

undefined4 FUN_0040745c(undefined2 param_1,undefined4 param_2,int param_3,undefined4 param_4)

{
  byte bVar1;
  uint *puVar2;
  uint uVar3;
  uint *puVar4;
  int iVar5;
  char *pcVar6;
  byte *pbVar7;
  uint uVar8;
  uint uVar9;
  undefined1 local_100 [5];
  undefined1 local_fb;
  
  puVar2 = (uint *)FUN_00407000(param_4);
  while (puVar4 = puVar2, (byte)*puVar4 != 0) {
    if ((byte)*puVar4 == 0xf) {
      bVar1 = *(byte *)((int)puVar4 + 1);
      uVar3 = (uint)*(byte *)((int)puVar4 + 2);
      puVar2 = (uint *)((int)puVar4 + 3);
      if ((bVar1 & 0x70) == 0x20) {
        uVar8 = 10;
        pcVar6 = local_100;
        uVar3 = (*(code *)(&PTR_LAB_0041030c)[*(byte *)puVar2])();
        FUN_0040c720(uVar3,pcVar6,uVar8);
        puVar2 = puVar4 + 1;
      }
      else {
        iVar5 = DAT_0041634c;
        if (((bVar1 & 0x70) == 0) || (iVar5 = DAT_00416350, (bVar1 & 0x70) == 0x10)) {
          if ((bVar1 & 0x80) == 0) {
            puVar4 = puVar4 + 1;
            uVar8 = (uint)*(byte *)puVar2;
          }
          else {
            uVar8 = *puVar2;
            puVar4 = (uint *)((int)puVar4 + 7);
          }
          puVar2 = (uint *)(iVar5 + uVar8);
          switch(bVar1 & 7) {
          case 0:
            FUN_0040c720((int)(char)*puVar2,local_100,10);
            break;
          case 1:
            FUN_0040c780((uint *)local_100,(uint *)*puVar2,uVar3);
            break;
          case 2:
            FUN_0040c780((uint *)local_100,puVar2,uVar3);
            break;
          case 3:
            FUN_0040c720(*puVar2,local_100,10);
            break;
          case 4:
            FUN_0040c720(*(uint *)*puVar2,local_100,10);
            break;
          case 5:
            local_100[0] = 0x35;
            local_100._1_4_ = (int)puVar2 - DAT_0041634c;
            local_fb = 0;
            uVar9 = 10;
            pbVar7 = local_100;
            uVar8 = FUN_00408e64(pbVar7);
            FUN_0040c720(uVar8,(char *)pbVar7,uVar9);
          }
          local_100[uVar3] = 0;
          pbVar7 = local_100;
          bVar1 = local_100[0];
          while (puVar2 = puVar4, bVar1 != 0) {
            FUN_00406f7c(param_1,&param_2,param_3,(uint)*pbVar7);
            bVar1 = pbVar7[1];
            pbVar7 = pbVar7 + 1;
          }
        }
      }
    }
    else {
      FUN_00406f7c(param_1,&param_2,param_3,(uint)(byte)*puVar4);
      puVar2 = (uint *)((int)puVar4 + 1);
    }
  }
  return 1;
}



/* Address: 00407670 */

undefined4 FUN_00407670(byte param_1)

{
  int iVar1;
  uint uVar2;
  byte *pbVar3;
  
  iVar1 = DAT_00416198;
  if (*(byte *)(DAT_00416198 + 5) != param_1) {
    *(byte *)(DAT_00416198 + 5) = param_1;
    uVar2 = (uint)*(ushort *)(iVar1 + 8);
    if (uVar2 != 0xffff) {
      *(byte *)(DAT_004161c0 + uVar2 * -0x10 + 3) = param_1;
    }
    uVar2 = (uint)*(ushort *)(iVar1 + 10);
    if (uVar2 != 0xffff) {
      pbVar3 = (byte *)(DAT_004161c0 + uVar2 * -0x10);
      pbVar3[3] = param_1;
      FUN_00408b04(pbVar3);
    }
  }
  return 1;
}



/* Address: 004076d8 */

undefined4 FUN_004076d8(void)

{
  FUN_004023d8(DAT_00416198);
  return 1;
}



/* Address: 004077ac */

void FUN_004077ac(char param_1)

{
  if ((param_1 == -0x7e) || (param_1 == -0x7d)) {
    DAT_00411c48 = DAT_00416364;
    DAT_00411c4c = DAT_00416360;
  }
  else {
    DAT_00411c48 = DAT_0041635c;
    DAT_00411c4c = DAT_00416358;
  }
  DAT_00411c40 = (uint)*(byte *)(DAT_00416198 + 6);
  DAT_00411c44 = (uint)*(byte *)(DAT_00416198 + 7);
  DAT_00411c54 = 0xffffffff;
  DAT_00411c50 = 0xffffffff;
  DAT_0041676c = 0;
  return;
}



/* Address: 00407820 */

undefined4 FUN_00407820(void)

{
  int iVar1;
  int iVar2;
  
  iVar2 = DAT_00411c40 - DAT_00411c48;
  if (iVar2 < 1) {
    iVar2 = -iVar2;
  }
  iVar1 = DAT_00411c44 - DAT_00411c4c;
  if (iVar1 < 1) {
    iVar1 = -iVar1;
  }
  if ((iVar2 == 0) && (iVar1 == 0)) {
    return 0;
  }
  if ((iVar2 == 0) || (3 < iVar1 / iVar2)) {
    if ((1 < iVar1) && (DAT_00411c50 = 4, DAT_00411c4c <= DAT_00411c44)) {
      DAT_00411c50 = 0;
    }
    DAT_00411c54 = 4;
    if (DAT_00411c4c <= DAT_00411c44) {
      DAT_00411c54 = 0;
    }
  }
  else if ((iVar1 == 0) || (3 < iVar2 / iVar1)) {
    if ((1 < iVar2) && (DAT_00411c50 = 2, DAT_00411c48 <= DAT_00411c40)) {
      DAT_00411c50 = 6;
    }
    DAT_00411c54 = 2;
    if (DAT_00411c48 <= DAT_00411c40) {
      DAT_00411c54 = 6;
    }
  }
  else {
    if (DAT_00411c40 < DAT_00411c48) {
      DAT_00411c50 = 3;
      if (DAT_00411c4c <= DAT_00411c44) {
        DAT_00411c50 = 1;
      }
    }
    else {
      DAT_00411c50 = 5;
      if (DAT_00411c4c <= DAT_00411c44) {
        DAT_00411c50 = 7;
      }
    }
    if (iVar1 < iVar2) {
      DAT_00411c54 = 2;
      if (DAT_00411c48 <= DAT_00411c40) {
        DAT_00411c54 = 6;
      }
    }
    else {
      DAT_00411c54 = 4;
      if (DAT_00411c4c <= DAT_00411c44) {
        DAT_00411c54 = 0;
      }
    }
  }
  DAT_0041676c = 1;
  return 1;
}



/* Address: 00407964 */

int FUN_00407964(byte param_1)

{
  int iVar1;
  int iVar2;
  int iVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  int local_c;
  int local_8;
  int local_4;
  
  local_c = DAT_00411c4c;
  local_8 = -1;
  local_4 = -1;
  iVar3 = DAT_00411c4c * DAT_00416168 + DAT_0041633c + DAT_00411c48;
  iVar5 = DAT_00411c48;
  do {
    iVar4 = iVar5;
    iVar2 = DAT_00411c40 - iVar4;
    if (iVar2 < 1) {
      iVar2 = -iVar2;
    }
    iVar1 = DAT_00411c44 - local_c;
    if (iVar1 < 1) {
      iVar1 = -iVar1;
    }
    iVar5 = iVar4;
    iVar6 = local_c;
    if (iVar1 < iVar2) {
      if ((iVar4 == 0) || ((int)*(char *)(iVar3 + -1) != (uint)param_1)) {
        if ((DAT_00416340 <= iVar4) || ((int)*(char *)(iVar3 + 1) != (uint)param_1)) {
          if ((local_c == 0) || ((int)*(char *)(iVar3 + DAT_00416344) != (uint)param_1)) {
            if ((DAT_00416348 <= local_c) || ((int)*(char *)(iVar3 + DAT_00416168) != (uint)param_1)
               ) {
              return iVar1;
            }
            goto LAB_00407b61;
          }
          goto LAB_00407b39;
        }
LAB_00407ba4:
        iVar5 = iVar4 + 1;
        iVar3 = iVar3 + 1;
      }
      else {
LAB_00407b7f:
        iVar5 = iVar4 + -1;
        iVar3 = iVar3 + -1;
      }
    }
    else {
      if ((local_c == 0) || ((int)*(char *)(iVar3 + DAT_00416344) != (uint)param_1)) {
        if ((local_c < DAT_00416348) && ((int)*(char *)(iVar3 + DAT_00416168) == (uint)param_1)) {
LAB_00407b61:
          iVar3 = iVar3 + DAT_00416168;
          iVar6 = local_c + 1;
          goto LAB_00407a42;
        }
        if ((iVar4 == 0) || ((int)*(char *)(iVar3 + -1) != (uint)param_1)) {
          if (DAT_00416340 <= iVar4) {
            return iVar1;
          }
          if ((int)*(char *)(iVar3 + 1) != (uint)param_1) {
            return iVar1;
          }
          goto LAB_00407ba4;
        }
        goto LAB_00407b7f;
      }
LAB_00407b39:
      iVar3 = iVar3 - DAT_00416168;
      iVar6 = local_c + -1;
    }
LAB_00407a42:
    if ((iVar5 == DAT_00411c40) && (iVar6 == DAT_00411c44)) {
      DAT_00411c54 = 2;
      if ((iVar4 <= iVar5) &&
         ((DAT_00411c54 = 6, iVar5 <= iVar4 && (DAT_00411c54 = 4, local_c <= iVar6)))) {
        DAT_00411c54 = 0;
      }
      if (local_8 != -1) {
        if (iVar6 < local_4) {
          DAT_00411c50 = 3;
          if ((local_8 <= iVar5) && (DAT_00411c50 = 5, iVar5 <= local_8)) {
            DAT_00411c50 = 4;
          }
        }
        else if (local_4 < iVar6) {
          DAT_00411c50 = 1;
          if ((local_8 <= iVar5) && (DAT_00411c50 = 7, iVar5 <= local_8)) {
            DAT_00411c50 = 0;
          }
        }
        else {
          DAT_00411c50 = 2;
          if (local_8 <= iVar5) {
            DAT_00411c50 = 6;
          }
        }
      }
      DAT_0041676c = 1;
      return 1;
    }
    local_4 = local_c;
    local_c = iVar6;
    local_8 = iVar4;
    if (param_1 == 4) {
      param_1 = 6;
    }
    else if (param_1 == 5) {
      param_1 = 4;
    }
    else if (param_1 == 6) {
      param_1 = 5;
    }
  } while( true );
}



/* Address: 00407bac */

undefined1 FUN_00407bac(void)

{
  undefined1 uVar1;
  int iVar2;
  char *pcVar3;
  int iVar4;
  
  iVar2 = 0;
  iVar4 = 0;
  uVar1 = 0;
  pcVar3 = DAT_0041633c;
  do {
    if (*pcVar3 == '\0') {
      if ((((iVar2 != 0) && (pcVar3[-1] == '\x06')) ||
          ((iVar2 < DAT_00416340 && (pcVar3[1] == '\x06')))) ||
         (((iVar4 != 0 && (pcVar3[DAT_00416344] == '\x06')) ||
          ((iVar4 < DAT_00416348 && (pcVar3[DAT_00416168] == '\x06')))))) {
        uVar1 = 4;
        *pcVar3 = '\x04';
      }
    }
    else if ((*pcVar3 == '\x02') &&
            (((((iVar2 != 0 && (pcVar3[-1] == '\x06')) ||
               ((iVar2 < DAT_00416340 && (pcVar3[1] == '\x06')))) ||
              ((iVar4 != 0 && (pcVar3[DAT_00416344] == '\x06')))) ||
             ((iVar4 < DAT_00416348 && (pcVar3[DAT_00416168] == '\x06')))))) {
      DAT_00411c48 = iVar2;
      DAT_00411c4c = iVar4;
      return 1;
    }
    iVar2 = iVar2 + 1;
    if (DAT_00416168 <= iVar2) {
      iVar4 = iVar4 + 1;
      if (DAT_0041616c <= iVar4) {
        return uVar1;
      }
      iVar2 = 0;
    }
    pcVar3 = pcVar3 + 1;
  } while( true );
}



/* Address: 00407c78 */

undefined1 FUN_00407c78(void)

{
  undefined1 uVar1;
  int iVar2;
  char *pcVar3;
  int iVar4;
  
  iVar2 = 0;
  iVar4 = 0;
  uVar1 = 0;
  pcVar3 = DAT_0041633c;
  do {
    if (*pcVar3 == '\0') {
      if ((((iVar2 != 0) && (pcVar3[-1] == '\x04')) ||
          ((iVar2 < DAT_00416340 && (pcVar3[1] == '\x04')))) ||
         (((iVar4 != 0 && (pcVar3[DAT_00416344] == '\x04')) ||
          ((iVar4 < DAT_00416348 && (pcVar3[DAT_00416168] == '\x04')))))) {
        uVar1 = 5;
        *pcVar3 = '\x05';
      }
    }
    else if ((*pcVar3 == '\x02') &&
            (((((iVar2 != 0 && (pcVar3[-1] == '\x04')) ||
               ((iVar2 < DAT_00416340 && (pcVar3[1] == '\x04')))) ||
              ((iVar4 != 0 && (pcVar3[DAT_00416344] == '\x04')))) ||
             ((iVar4 < DAT_00416348 && (pcVar3[DAT_00416168] == '\x04')))))) {
      DAT_00411c48 = iVar2;
      DAT_00411c4c = iVar4;
      return 1;
    }
    iVar2 = iVar2 + 1;
    if (DAT_00416168 <= iVar2) {
      iVar4 = iVar4 + 1;
      if (DAT_0041616c <= iVar4) {
        return uVar1;
      }
      iVar2 = 0;
    }
    pcVar3 = pcVar3 + 1;
  } while( true );
}



/* Address: 00407d44 */

undefined1 FUN_00407d44(void)

{
  undefined1 uVar1;
  int iVar2;
  char *pcVar3;
  int iVar4;
  
  iVar2 = 0;
  iVar4 = 0;
  uVar1 = 0;
  pcVar3 = DAT_0041633c;
  do {
    if (*pcVar3 == '\0') {
      if ((((iVar2 != 0) && (pcVar3[-1] == '\x05')) ||
          ((iVar2 < DAT_00416340 && (pcVar3[1] == '\x05')))) ||
         (((iVar4 != 0 && (pcVar3[DAT_00416344] == '\x05')) ||
          ((iVar4 < DAT_00416348 && (pcVar3[DAT_00416168] == '\x05')))))) {
        uVar1 = 6;
        *pcVar3 = '\x06';
      }
    }
    else if ((*pcVar3 == '\x02') &&
            (((((iVar2 != 0 && (pcVar3[-1] == '\x05')) ||
               ((iVar2 < DAT_00416340 && (pcVar3[1] == '\x05')))) ||
              ((iVar4 != 0 && (pcVar3[DAT_00416344] == '\x05')))) ||
             ((iVar4 < DAT_00416348 && (pcVar3[DAT_00416168] == '\x05')))))) {
      DAT_00411c48 = iVar2;
      DAT_00411c4c = iVar4;
      return 1;
    }
    iVar2 = iVar2 + 1;
    if (DAT_00416168 <= iVar2) {
      iVar4 = iVar4 + 1;
      if (DAT_0041616c <= iVar4) {
        return uVar1;
      }
      iVar2 = 0;
    }
    pcVar3 = pcVar3 + 1;
  } while( true );
}



/* Address: 00407e10 */

int FUN_00407e10(char param_1)

{
  char cVar1;
  int iVar2;
  
  *(undefined1 *)(DAT_00411c44 * DAT_00416168 + DAT_0041633c + DAT_00411c40) = 6;
  while( true ) {
    cVar1 = FUN_00407bac();
    if (cVar1 == '\0') {
      return 0;
    }
    if (cVar1 == '\x01') {
      if (param_1 == '\0') {
        iVar2 = FUN_00407964(6);
      }
      else {
        iVar2 = FUN_00407820();
      }
      return iVar2;
    }
    cVar1 = FUN_00407c78();
    if (cVar1 == '\0') {
      return 0;
    }
    if (cVar1 == '\x01') break;
    cVar1 = FUN_00407d44();
    if (cVar1 == '\0') {
      return 0;
    }
    if (cVar1 == '\x01') {
      if (param_1 == '\0') {
        iVar2 = FUN_00407964(5);
      }
      else {
        iVar2 = FUN_00407820();
      }
      return iVar2;
    }
  }
  if (param_1 == '\0') {
    iVar2 = FUN_00407964(4);
  }
  else {
    iVar2 = FUN_00407820();
  }
  return iVar2;
}



/* Address: 00407eb0 */

void FUN_00407eb0(char param_1)

{
  undefined1 uVar1;
  char *pcVar2;
  int iVar3;
  undefined1 *puVar4;
  int iVar5;
  
  iVar5 = 0;
  puVar4 = DAT_0041633c;
  do {
    pcVar2 = (char *)(DAT_004161d0 + (iVar5 << ((byte)DAT_004161d4 & 0x1f)) * 2);
    iVar3 = 0;
    do {
      uVar1 = 2;
      if (*pcVar2 != param_1) {
        uVar1 = 0;
      }
      *puVar4 = uVar1;
      pcVar2 = pcVar2 + 2;
      puVar4 = puVar4 + 1;
      iVar3 = iVar3 + 1;
    } while (iVar3 < DAT_00416168);
    iVar5 = iVar5 + 1;
  } while (iVar5 < DAT_0041616c);
  FUN_00407e10('\x01');
  return;
}



/* Address: 00407f04 */

void FUN_00407f04(char param_1)

{
  undefined1 uVar1;
  char *pcVar2;
  int iVar3;
  undefined1 *puVar4;
  int iVar5;
  
  iVar5 = 0;
  puVar4 = DAT_0041633c;
  do {
    pcVar2 = (char *)(DAT_004161d0 + (iVar5 << ((byte)DAT_004161d4 & 0x1f)) * 2);
    iVar3 = 0;
    do {
      uVar1 = 0;
      if ((*pcVar2 != -2) && (uVar1 = 2, *pcVar2 != param_1)) {
        uVar1 = 3;
      }
      *puVar4 = uVar1;
      pcVar2 = pcVar2 + 2;
      puVar4 = puVar4 + 1;
      iVar3 = iVar3 + 1;
    } while (iVar3 < DAT_00416168);
    iVar5 = iVar5 + 1;
  } while (iVar5 < DAT_0041616c);
  FUN_00407e10('\0');
  return;
}



/* Address: 00407f60 */

void FUN_00407f60(void)

{
  undefined1 uVar1;
  char *pcVar2;
  int iVar3;
  undefined1 *puVar4;
  int iVar5;
  
  iVar5 = 0;
  puVar4 = DAT_0041633c;
  do {
    pcVar2 = (char *)(DAT_004161d0 + (iVar5 << ((byte)DAT_004161d4 & 0x1f)) * 2);
    iVar3 = 0;
    do {
      uVar1 = 0;
      if (*pcVar2 != -2) {
        uVar1 = 3;
      }
      *puVar4 = uVar1;
      pcVar2 = pcVar2 + 2;
      puVar4 = puVar4 + 1;
      iVar3 = iVar3 + 1;
    } while (iVar3 < DAT_00416168);
    iVar5 = iVar5 + 1;
  } while (iVar5 < DAT_0041616c);
  DAT_0041633c[DAT_00411c48 + DAT_00411c4c * DAT_00416168] = 2;
  FUN_00407e10('\0');
  return;
}



/* Address: 00407fcc */

void FUN_00407fcc(char param_1)

{
  undefined1 uVar1;
  char *pcVar2;
  int iVar3;
  undefined1 *puVar4;
  int iVar5;
  
  iVar5 = 0;
  puVar4 = DAT_0041633c;
  do {
    pcVar2 = (char *)(DAT_004161d0 + (iVar5 << ((byte)DAT_004161d4 & 0x1f)) * 2);
    iVar3 = 0;
    do {
      uVar1 = 0;
      if (*pcVar2 != param_1) {
        uVar1 = 3;
      }
      *puVar4 = uVar1;
      pcVar2 = pcVar2 + 2;
      puVar4 = puVar4 + 1;
      iVar3 = iVar3 + 1;
    } while (iVar3 < DAT_00416168);
    iVar5 = iVar5 + 1;
  } while (iVar5 < DAT_0041616c);
  DAT_0041633c[DAT_00411c48 + DAT_00411c4c * DAT_00416168] = 2;
  FUN_00407e10('\0');
  return;
}



/* Address: 0040803c */

undefined4 FUN_0040803c(char param_1,char param_2,char param_3)

{
  char cVar1;
  undefined4 uVar2;
  char extraout_DL;
  
  FUN_004077ac(param_3);
  if ((extraout_DL == -0x7e) || (extraout_DL == -0x7d)) {
    cVar1 = DAT_00416765;
    if (extraout_DL != DAT_00416766) {
      return 0;
    }
  }
  else {
    cVar1 = DAT_0041676b;
    if (extraout_DL != '\x0e') {
      if (param_1 == -1) {
        uVar2 = FUN_00407eb0(param_2);
      }
      else {
        uVar2 = FUN_00407f04(param_2);
      }
      return uVar2;
    }
  }
  if ((param_2 != -1) && (param_2 != cVar1)) {
    return 0;
  }
  if (param_1 == -1) {
    uVar2 = FUN_00407820();
    return uVar2;
  }
  if (param_1 == -2) {
    uVar2 = FUN_00407f60();
    return uVar2;
  }
  uVar2 = FUN_00407fcc(param_2);
  return uVar2;
}



/* Address: 004080d8 */

void FUN_004080d8(int param_1)

{
  int iVar1;
  byte *pbVar2;
  char *local_8;
  int local_4;
  
  local_8 = DAT_0041633c;
  local_4 = 0;
  do {
    pbVar2 = (byte *)(DAT_004161d0 + (local_4 << ((byte)DAT_004161d4 & 0x1f)) * 2);
    iVar1 = 0;
    do {
      *local_8 = (-((1 << (*pbVar2 & 7) & (uint)*(byte *)(((int)(uint)*pbVar2 >> 3) + param_1)) == 0
                   ) & 0xfeU) + 2;
      pbVar2 = pbVar2 + 2;
      local_8 = local_8 + 1;
      iVar1 = iVar1 + 1;
    } while (iVar1 < DAT_00416168);
    local_4 = local_4 + 1;
  } while (local_4 < DAT_0041616c);
  FUN_00407e10('\x01');
  return;
}



/* Address: 0040816c */

void FUN_0040816c(int param_1)

{
  byte bVar1;
  char cVar2;
  int iVar3;
  byte *pbVar4;
  char *local_8;
  int local_4;
  
  local_8 = DAT_0041633c;
  local_4 = 0;
  do {
    pbVar4 = (byte *)(DAT_004161d0 + (local_4 << ((byte)DAT_004161d4 & 0x1f)) * 2);
    iVar3 = 0;
    do {
      bVar1 = *pbVar4;
      cVar2 = '\0';
      if (bVar1 != 0xfe) {
        cVar2 = (-((1 << (bVar1 & 7) & (uint)*(byte *)(((int)(uint)bVar1 >> 3) + param_1)) == 0) &
                1U) + 2;
      }
      *local_8 = cVar2;
      pbVar4 = pbVar4 + 2;
      local_8 = local_8 + 1;
      iVar3 = iVar3 + 1;
    } while (iVar3 < DAT_00416168);
    local_4 = local_4 + 1;
  } while (local_4 < DAT_0041616c);
  FUN_00407e10('\0');
  return;
}



/* Address: 00408210 */

void FUN_00408210(int param_1)

{
  int iVar1;
  byte *pbVar2;
  byte *local_8;
  int local_4;
  
  local_8 = DAT_0041633c;
  local_4 = 0;
  do {
    pbVar2 = (byte *)(DAT_004161d0 + (local_4 << ((byte)DAT_004161d4 & 0x1f)) * 2);
    iVar1 = 0;
    do {
      *local_8 = -((1 << (*pbVar2 & 7) & (uint)*(byte *)(((int)(uint)*pbVar2 >> 3) + param_1)) == 0)
                 & 3;
      pbVar2 = pbVar2 + 2;
      local_8 = local_8 + 1;
      iVar1 = iVar1 + 1;
    } while (iVar1 < DAT_00416168);
    local_4 = local_4 + 1;
  } while (local_4 < DAT_0041616c);
  DAT_0041633c[DAT_00411c48 + DAT_00411c4c * DAT_00416168] = 2;
  FUN_00407e10('\0');
  return;
}



/* Address: 004082c0 */

undefined4 FUN_004082c0(char param_1,byte param_2,char param_3)

{
  int iVar1;
  uint uVar2;
  undefined4 uVar3;
  
  FUN_004077ac(param_3);
  iVar1 = *(int *)((uint)param_2 * 0x10 + DAT_004161cc);
  if ((param_3 == -0x7e) || (param_3 == -0x7d)) {
    if (param_3 != DAT_00416766) {
      return 0;
    }
    uVar2 = (uint)*(byte *)(((int)(uint)DAT_00416765 >> 3) + iVar1) & 1 << (DAT_00416765 & 7);
  }
  else {
    if (param_3 != '\x0e') {
      if (param_1 == -1) {
        uVar3 = FUN_004080d8(iVar1);
      }
      else {
        uVar3 = FUN_0040816c(iVar1);
      }
      return uVar3;
    }
    uVar2 = (uint)*(byte *)(((int)(uint)DAT_0041676b >> 3) + iVar1) & 1 << (DAT_0041676b & 7);
  }
  if (uVar2 == 0) {
    return 0;
  }
  if (param_1 == -1) {
    uVar3 = FUN_00407820();
    return uVar3;
  }
  if (param_1 != -2) {
    uVar3 = FUN_00408210(iVar1);
    return uVar3;
  }
  uVar3 = FUN_00407f60();
  return uVar3;
}



/* Address: 004083a8 */

void FUN_004083a8(undefined4 param_1)

{
  switch(param_1) {
  case 3:
    FUN_0040803c(-1,-1,'\x0e');
    return;
  case 4:
    FUN_0040803c(-1,-2,'\x0e');
    return;
  case 5:
    FUN_0040803c(-2,-2,'\x0e');
    return;
  case 6:
    FUN_0040803c(-1,-1,-0x7e);
    return;
  case 7:
    FUN_0040803c(-1,-2,-0x7e);
    return;
  case 8:
    FUN_0040803c(-2,-2,-0x7e);
    return;
  case 9:
    FUN_0040803c(-1,-1,-0x7d);
    return;
  case 10:
    FUN_0040803c(-1,-2,-0x7d);
    return;
  case 0xb:
    FUN_0040803c(-2,-2,-0x7d);
    return;
  default:
    return;
  }
}



/* Address: 0040844c */

void FUN_0040844c(undefined4 param_1,char param_2)

{
  switch(param_1) {
  case 1:
    FUN_0040803c(-1,param_2,'\0');
    return;
  case 2:
    FUN_0040803c(-2,param_2,'\0');
    return;
  case 3:
    FUN_0040803c(-1,param_2,'\x0e');
    return;
  case 4:
    FUN_0040803c(-2,param_2,'\x0e');
    return;
  case 5:
    FUN_0040803c(param_2,param_2,'\x0e');
    return;
  case 6:
    FUN_0040803c(-1,param_2,-0x7e);
    return;
  case 7:
    FUN_0040803c(-2,param_2,-0x7e);
    return;
  case 8:
    FUN_0040803c(param_2,param_2,-0x7e);
    return;
  case 9:
    FUN_0040803c(-1,param_2,-0x7d);
    return;
  case 10:
    FUN_0040803c(-2,param_2,-0x7d);
    return;
  case 0xb:
    FUN_0040803c(param_2,param_2,-0x7d);
    return;
  default:
    return;
  }
}



/* Address: 0040852c */

void FUN_0040852c(undefined4 param_1,byte param_2)

{
  switch(param_1) {
  case 1:
    FUN_004082c0(-1,param_2,'\0');
    return;
  case 2:
    FUN_004082c0(-2,param_2,'\0');
    return;
  case 3:
    FUN_004082c0(-1,param_2,'\x0e');
    return;
  case 4:
    FUN_004082c0(-2,param_2,'\x0e');
    return;
  case 5:
    FUN_004082c0(param_2,param_2,'\x0e');
    return;
  case 6:
    FUN_004082c0(-1,param_2,-0x7e);
    return;
  case 7:
    FUN_004082c0(-2,param_2,-0x7e);
    return;
  case 8:
    FUN_004082c0(param_2,param_2,-0x7e);
    return;
  case 9:
    FUN_004082c0(-1,param_2,-0x7d);
    return;
  case 10:
    FUN_004082c0(-2,param_2,-0x7d);
    return;
  case 0xb:
    FUN_004082c0(param_2,param_2,-0x7d);
    return;
  default:
    return;
  }
}



/* Address: 004087cc */

bool __thiscall FUN_004087cc(char param_1,undefined4 param_2)

{
  switch(param_2) {
  case 0:
    param_1 = DAT_004162b6;
    break;
  case 1:
    param_1 = DAT_004162b4;
    break;
  case 2:
    param_1 = DAT_00416763;
    break;
  case 3:
    param_1 = DAT_004162b7;
  }
  return (bool)('\x01' - (param_1 == '\0'));
}



/* Address: 00408808 */

undefined4 FUN_00408808(undefined4 param_1)

{
  switch(param_1) {
  case 0:
  case 1:
    DAT_004162b6 = (char)param_1;
    break;
  case 2:
    DAT_004162b4 = 0;
    DAT_00416762 = 0;
    break;
  case 3:
    DAT_004162b4 = 1;
    DAT_00416762 = DAT_0041676e;
    break;
  case 4:
    DAT_00416763 = 0;
    break;
  case 5:
    DAT_00416763 = DAT_0041676f;
    break;
  case 6:
  case 7:
    DAT_004162b7 = (char)param_1 + -6;
  }
  return 1;
}



/* Address: 0040893c */

void FUN_0040893c(void)

{
  FUN_0040a268();
  FUN_0040a328();
  return;
}



/* Address: 00408948 */

void FUN_00408948(void)

{
  FUN_0040a0f4(&DAT_0041676f,&DAT_0041676e,0);
  if (DAT_004162c0 != -1) {
    FUN_00406cd8(DAT_004162c0);
  }
  return;
}



/* Address: 00408a04 */

void FUN_00408a04(byte *param_1)

{
  undefined4 *puVar1;
  short *psVar2;
  byte *pbVar3;
  short sVar4;
  short local_14;
  short local_10;
  
  puVar1 = *(undefined4 **)(param_1 + 0xc);
  psVar2 = (short *)*puVar1;
  local_14 = (ushort)param_1[4] * (short)DAT_00416204;
  local_10 = (ushort)param_1[5] * (short)DAT_00416208;
  if ((*(ushort *)(param_1 + 6) != 0xffff) &&
     (pbVar3 = (byte *)(DAT_004161c0 + (uint)*(ushort *)(param_1 + 6) * -0x10), (*pbVar3 & 4) != 0))
  {
    local_14 = local_14 +
               (short)((int)(((uint)pbVar3[6] - (uint)param_1[4]) * DAT_00416204 * (param_1[1] + 1))
                      / (int)(uint)param_1[2]);
    local_10 = local_10 +
               (short)((int)(((uint)pbVar3[7] - (uint)param_1[5]) * DAT_00416208 * (param_1[1] + 1))
                      / (int)(uint)param_1[2]);
  }
  if ((*param_1 & 8) == 0) {
    sVar4 = *(short *)(puVar1 + 1);
  }
  else {
    sVar4 = (*psVar2 - (short)DAT_00416204) - *(short *)(puVar1 + 1);
  }
  *(short *)(param_1 + 8) = local_14 - sVar4;
  if ((*param_1 & 0x10) == 0) {
    sVar4 = *(short *)((int)puVar1 + 6);
  }
  else {
    sVar4 = (psVar2[1] - (short)DAT_00416208) - *(short *)((int)puVar1 + 6);
  }
  *(short *)(param_1 + 10) = local_10 - sVar4;
  return;
}



/* Address: 00408b04 */

void FUN_00408b04(byte *param_1)

{
  undefined4 *puVar1;
  uint uVar2;
  uint uVar3;
  
  puVar1 = *(undefined4 **)(param_1 + 0xc);
  uVar2 = (uint)*(short *)(param_1 + 8);
  uVar3 = (uint)*(short *)(param_1 + 10);
  if ((*param_1 & 0x40) != 0) {
    uVar2 = uVar2 - (int)*(short *)(puVar1 + 1);
    uVar3 = uVar3 - (int)*(short *)((int)puVar1 + 6);
  }
  FUN_0040426c(((ushort *)*puVar1)[1] + uVar3,*(ushort *)*puVar1 + uVar2,uVar3,uVar2);
  return;
}



/* Address: 00408b48 */

void FUN_00408b48(int param_1)

{
  FUN_00408b04((byte *)(DAT_004161c0 + param_1 * -0x10));
  return;
}



/* Address: 00408b60 */

void FUN_00408b60(char param_1,byte *param_2,int param_3)

{
  byte bVar1;
  int iVar2;
  
  *param_2 = *param_2 & 0xe7;
  param_2[1] = 0;
  if (*(char *)(param_3 + 2) == '\x01') {
    *(undefined4 *)(param_2 + 0xc) = *(undefined4 *)(param_3 + 4);
    if (DAT_0041675e == -0x80) {
      if ((*(byte *)(param_3 + 1) & 2) != 0) {
        *param_2 = *param_2 | 8;
      }
    }
    else if ((DAT_0041675e == '@') && ((*(byte *)(param_3 + 1) & 4) != 0)) {
      *param_2 = *param_2 | 0x10;
    }
    goto LAB_00408d22;
  }
  if (DAT_0041675e == '\x10') {
    iVar2 = 1;
    if ((DAT_004161a4 == DAT_004161ac) && ((*(byte *)(param_3 + 1) & 8) != 0)) goto LAB_00408cba;
  }
  else if (DAT_0041675e == ' ') {
    iVar2 = 3;
    if (DAT_004161ac < DAT_004161a4) {
      iVar2 = 2;
    }
    else if (DAT_004161a4 < DAT_004161ac) {
      iVar2 = 4;
      if ((*(byte *)(param_3 + 1) & 4) != 0) {
        iVar2 = 2;
        *param_2 = *param_2 | 0x10;
      }
    }
    else if (((*(byte *)(param_3 + 1) & 8) != 0) && (DAT_004161a0 == DAT_004161a8))
    goto LAB_00408cba;
  }
  else if (DAT_0041675e == '@') {
    iVar2 = 5;
    if ((DAT_004161a4 == DAT_004161ac) && ((*(byte *)(param_3 + 1) & 8) != 0)) {
LAB_00408cba:
      iVar2 = 0;
    }
    else if ((*(byte *)(param_3 + 1) & 4) != 0) {
      iVar2 = 1;
      *param_2 = *param_2 | 0x10;
    }
  }
  else {
    iVar2 = 7;
    if (DAT_004161a4 == DAT_004161ac) {
      if (((*(byte *)(param_3 + 1) & 8) != 0) && (DAT_004161a0 == DAT_004161a8)) goto LAB_00408cba;
      if ((*(byte *)(param_3 + 1) & 2) != 0) {
        iVar2 = 3;
        *param_2 = *param_2 | 8;
      }
    }
    else {
      if (DAT_004161ac < DAT_004161a4) {
        bVar1 = *(byte *)(param_3 + 1);
      }
      else {
        iVar2 = 6;
        if ((*(byte *)(param_3 + 1) & 4) == 0) {
          if ((*(byte *)(param_3 + 1) & 2) != 0) {
            iVar2 = 4;
            *param_2 = *param_2 | 8;
          }
          goto LAB_00408d19;
        }
        *param_2 = *param_2 | 0x10;
        bVar1 = *(byte *)(param_3 + 1);
      }
      iVar2 = 8;
      if ((bVar1 & 2) != 0) {
        iVar2 = 2;
        *param_2 = *param_2 | 8;
      }
    }
  }
LAB_00408d19:
  *(undefined4 *)(param_2 + 0xc) = *(undefined4 *)(*(int *)(param_3 + 4) + iVar2 * 4);
LAB_00408d22:
  if (param_1 == '\0') {
    param_2[4] = (byte)DAT_004161a8;
    param_2[5] = (byte)DAT_004161ac;
    FUN_00408a04(param_2);
  }
  else {
    *param_2 = *param_2 | 0x40;
  }
  FUN_00408b04(param_2);
  return;
}



/* Address: 00408d50 */

int FUN_00408d50(int param_1)

{
  int iVar1;
  int iVar2;
  uint *puVar3;
  
  iVar1 = DAT_004161dc + DAT_00416184 * 8;
  iVar2 = iVar1 + -8;
  if (*(uint *)(param_1 + 0xc) < *(uint *)(iVar1 + -4)) {
    puVar3 = (uint *)(iVar1 + -4);
    do {
      puVar3 = puVar3 + -2;
      iVar2 = iVar2 + -8;
    } while (*(uint *)(param_1 + 0xc) < *puVar3);
  }
  return iVar2;
}



/* Address: 00408d88 */

void FUN_00408d88(byte *param_1)

{
  int iVar1;
  byte *pbVar2;
  
  if (*(ushort *)(param_1 + 10) != 0xffff) {
    pbVar2 = (byte *)(DAT_004161c0 + (uint)*(ushort *)(param_1 + 10) * -0x10);
    iVar1 = FUN_00408d50((int)pbVar2);
    FUN_00408b04(pbVar2);
    if ((DAT_004161a8 != DAT_004161a0) || (DAT_004161ac != DAT_004161a4)) {
      *param_1 = *param_1 | 4;
    }
    FUN_00408b60('\0',pbVar2,iVar1);
  }
  return;
}



/* Address: 00408de8 */

undefined4 FUN_00408de8(int param_1,int param_2,int param_3)

{
  int iVar1;
  
  if ((((*(ushort *)(param_3 + 10) != 0xffff) &&
       (iVar1 = DAT_004161c0 + (uint)*(ushort *)(param_3 + 10) * -0x10,
       *(short *)(iVar1 + 8) <= param_2)) && (*(short *)(iVar1 + 10) <= param_1)) &&
     ((param_2 <=
       (int)((int)*(short *)(iVar1 + 8) + (uint)*(ushort *)**(undefined4 **)(iVar1 + 0xc)) &&
      (iVar1 = (int)*(short *)(iVar1 + 10) + (uint)((ushort *)**(undefined4 **)(iVar1 + 0xc))[1],
      param_1 <= iVar1)))) {
    return CONCAT31((int3)((uint)iVar1 >> 8),1);
  }
  return 0;
}



/* Address: 00408e4c */

void FUN_00408e4c(uint param_1,undefined1 *param_2)

{
  FUN_0040d2a4(param_2,0,param_1);
  return;
}



/* Address: 00408e64 */

void FUN_00408e64(byte *param_1)

{
                    /* WARNING: Could not recover jumptable at 0x00408e7c. Too many branches */
                    /* WARNING: Treating indirect jump as call */
  (*(code *)(&PTR_LAB_004104f4)[*param_1])();
  return;
}



/* Address: 00409079 */

undefined4 FUN_00409079(void)

{
  return 1;
}



/* Address: 0040907f */

void FUN_0040907f(void)

{
  return;
}



/* Address: 00409080 */

undefined4 FUN_00409080(int param_1,int *param_2)

{
  int iVar1;
  
  DAT_00411c5c = *(undefined4 *)(param_1 + 0xc);
  DAT_00411c60 = *(undefined4 *)(param_1 + 0x10);
  iVar1 = param_2[3];
  DAT_00411c58 = iVar1;
  if (iVar1 == 0) {
    DAT_00411c58 = param_2[1];
    param_2[3] = DAT_00411c58;
    param_2[1] = param_2[2] + DAT_00411c58;
    iVar1 = param_2[2];
    *param_2 = *param_2 - iVar1;
  }
  return CONCAT31((int3)((uint)iVar1 >> 8),1);
}



/* Address: 004090c4 */

void FUN_004090c4(int param_1,int param_2,int param_3,int param_4,undefined4 param_5,int param_6,
                 int param_7,int param_8,int param_9)

{
  bool bVar1;
  uint uVar2;
  uint uVar3;
  int iVar4;
  uint uVar5;
  int iVar6;
  int iVar7;
  int iVar8;
  undefined4 *puVar9;
  undefined4 *puVar10;
  
  uVar2 = param_2 - param_4;
  iVar4 = param_1 - param_3;
  puVar10 = (undefined4 *)(((DAT_00411c60 + -1) - param_3) * DAT_00411c5c + param_4 + DAT_00411c58);
  puVar9 = (undefined4 *)((-param_7 * param_6 - param_8) + param_9);
  iVar6 = DAT_00411c5c + uVar2;
  iVar7 = param_6 - uVar2;
  uVar3 = uVar2 >> 2;
  uVar5 = uVar3;
  if ((uVar2 & 3) == 0) {
    do {
      for (; uVar5 != 0; uVar5 = uVar5 - 1) {
        *puVar10 = *puVar9;
        puVar9 = puVar9 + 1;
        puVar10 = puVar10 + 1;
      }
      puVar10 = (undefined4 *)((int)puVar10 - iVar6);
      puVar9 = (undefined4 *)((int)puVar9 + iVar7);
      iVar8 = iVar4 + -1;
      bVar1 = 0 < iVar4;
      uVar5 = uVar3;
      iVar4 = iVar8;
    } while (iVar8 != 0 && bVar1);
    return;
  }
  if (((byte)uVar2 & 3) >> 1 == 0) {
    do {
      for (; uVar5 != 0; uVar5 = uVar5 - 1) {
        *puVar10 = *puVar9;
        puVar9 = puVar9 + 1;
        puVar10 = puVar10 + 1;
      }
      *(undefined1 *)puVar10 = *(undefined1 *)puVar9;
      puVar10 = (undefined4 *)((int)puVar10 + (1 - iVar6));
      puVar9 = (undefined4 *)((int)puVar9 + iVar7 + 1);
      iVar8 = iVar4 + -1;
      bVar1 = 0 < iVar4;
      uVar5 = uVar3;
      iVar4 = iVar8;
    } while (iVar8 != 0 && bVar1);
    return;
  }
  if (!(bool)((byte)uVar2 & 1)) {
    do {
      for (; uVar5 != 0; uVar5 = uVar5 - 1) {
        *puVar10 = *puVar9;
        puVar9 = puVar9 + 1;
        puVar10 = puVar10 + 1;
      }
      *(undefined2 *)puVar10 = *(undefined2 *)puVar9;
      puVar10 = (undefined4 *)((int)puVar10 + (2 - iVar6));
      puVar9 = (undefined4 *)((int)puVar9 + iVar7 + 2);
      iVar8 = iVar4 + -1;
      bVar1 = 0 < iVar4;
      uVar5 = uVar3;
      iVar4 = iVar8;
    } while (iVar8 != 0 && bVar1);
    return;
  }
  do {
    for (; uVar5 != 0; uVar5 = uVar5 - 1) {
      *puVar10 = *puVar9;
      puVar9 = puVar9 + 1;
      puVar10 = puVar10 + 1;
    }
    *(undefined2 *)puVar10 = *(undefined2 *)puVar9;
    *(undefined1 *)((int)puVar10 + 2) = *(undefined1 *)((int)puVar9 + 2);
    puVar10 = (undefined4 *)((int)puVar10 + (3 - iVar6));
    puVar9 = (undefined4 *)((int)puVar9 + iVar7 + 3);
    iVar8 = iVar4 + -1;
    bVar1 = 0 < iVar4;
    uVar5 = uVar3;
    iVar4 = iVar8;
  } while (iVar8 != 0 && bVar1);
  return;
}



/* Address: 00409174 */

void FUN_00409174(int param_1,int param_2,int param_3,int param_4,undefined4 param_5,int param_6,
                 int param_7,int param_8,int param_9)

{
  bool bVar1;
  char cVar2;
  int iVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  int iVar7;
  char *pcVar8;
  char *pcVar9;
  char *pcVar10;
  char *pcVar11;
  char *pcVar12;
  char *pcVar13;
  char *pcVar14;
  
  DAT_00411c64 = param_2 - param_4;
  pcVar12 = (char *)(((DAT_00411c60 + -1) - param_3) * DAT_00411c5c + param_4 + DAT_00411c58);
  pcVar8 = (char *)((-param_7 * param_6 - param_8) + param_9);
  iVar5 = DAT_00411c5c + DAT_00411c64;
  iVar6 = param_6 - DAT_00411c64;
  iVar7 = param_1 - param_3;
  do {
    cVar2 = *pcVar8;
    pcVar8 = pcVar8 + 1;
    iVar4 = DAT_00411c64;
    iVar3 = DAT_00411c64;
    pcVar11 = pcVar8;
    pcVar14 = pcVar12;
    if (cVar2 == '\0') goto LAB_00409261;
LAB_004091d9:
    *pcVar12 = cVar2;
    pcVar13 = pcVar12 + 1;
    pcVar9 = pcVar8;
    if (iVar4 + -1 != 0) {
      pcVar9 = pcVar8 + 1;
      iVar3 = iVar4 + -1;
      pcVar11 = pcVar9;
      pcVar14 = pcVar13;
      if (*pcVar8 == '\0') goto LAB_00409261;
      *pcVar13 = *pcVar8;
      pcVar13 = pcVar12 + 2;
      if (iVar4 + -2 == 0) goto LAB_004092ce;
      pcVar10 = pcVar8 + 2;
      iVar3 = iVar4 + -2;
      pcVar11 = pcVar10;
      pcVar14 = pcVar13;
      if (*pcVar9 == '\0') goto LAB_00409261;
      *pcVar13 = *pcVar9;
      pcVar13 = pcVar12 + 3;
      pcVar9 = pcVar10;
      if (iVar4 + -3 == 0) goto LAB_004092ce;
      pcVar9 = pcVar8 + 3;
      iVar3 = iVar4 + -3;
      pcVar11 = pcVar9;
      pcVar14 = pcVar13;
      if (*pcVar10 == '\0') goto LAB_00409261;
      *pcVar13 = *pcVar10;
      pcVar13 = pcVar12 + 4;
      if (iVar4 + -4 == 0) goto LAB_004092ce;
      pcVar10 = pcVar8 + 4;
      iVar3 = iVar4 + -4;
      pcVar11 = pcVar10;
      pcVar14 = pcVar13;
      if (*pcVar9 == '\0') goto LAB_00409261;
      *pcVar13 = *pcVar9;
      pcVar13 = pcVar12 + 5;
      pcVar9 = pcVar10;
      if (iVar4 + -5 == 0) goto LAB_004092ce;
      pcVar9 = pcVar8 + 5;
      iVar3 = iVar4 + -5;
      pcVar11 = pcVar9;
      pcVar14 = pcVar13;
      if (*pcVar10 == '\0') goto LAB_00409261;
      *pcVar13 = *pcVar10;
      pcVar13 = pcVar12 + 6;
      if (iVar4 + -6 == 0) goto LAB_004092ce;
      pcVar10 = pcVar8 + 6;
      iVar3 = iVar4 + -6;
      pcVar11 = pcVar10;
      pcVar14 = pcVar13;
      if (*pcVar9 == '\0') goto LAB_00409261;
      *pcVar13 = *pcVar9;
      pcVar13 = pcVar12 + 7;
      pcVar9 = pcVar10;
      if (iVar4 + -7 == 0) goto LAB_004092ce;
      pcVar8 = pcVar8 + 7;
      iVar3 = iVar4 + -7;
      pcVar11 = pcVar8;
      pcVar14 = pcVar13;
      if (*pcVar10 == '\0') goto LAB_00409261;
      *pcVar13 = *pcVar10;
      pcVar14 = pcVar12;
      iVar3 = iVar4;
      do {
        iVar4 = iVar3 + -8;
        pcVar13 = pcVar14 + 8;
        pcVar9 = pcVar8;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar8;
        pcVar8 = pcVar8 + 1;
        iVar3 = iVar4;
        pcVar11 = pcVar8;
        pcVar12 = pcVar13;
        pcVar14 = pcVar13;
        if (cVar2 != '\0') break;
LAB_00409261:
        pcVar13 = pcVar14 + 1;
        iVar4 = iVar3 + -1;
        pcVar9 = pcVar11;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar11;
        pcVar8 = pcVar11 + 1;
        pcVar12 = pcVar13;
        if (cVar2 != '\0') break;
        pcVar13 = pcVar14 + 2;
        iVar4 = iVar3 + -2;
        pcVar9 = pcVar8;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar8;
        pcVar8 = pcVar11 + 2;
        pcVar12 = pcVar13;
        if (cVar2 != '\0') break;
        pcVar13 = pcVar14 + 3;
        iVar4 = iVar3 + -3;
        pcVar9 = pcVar8;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar8;
        pcVar8 = pcVar11 + 3;
        pcVar12 = pcVar13;
        if (cVar2 != '\0') break;
        pcVar13 = pcVar14 + 4;
        iVar4 = iVar3 + -4;
        pcVar9 = pcVar8;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar8;
        pcVar8 = pcVar11 + 4;
        pcVar12 = pcVar13;
        if (cVar2 != '\0') break;
        pcVar13 = pcVar14 + 5;
        iVar4 = iVar3 + -5;
        pcVar9 = pcVar8;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar8;
        pcVar8 = pcVar11 + 5;
        pcVar12 = pcVar13;
        if (cVar2 != '\0') break;
        pcVar13 = pcVar14 + 6;
        iVar4 = iVar3 + -6;
        pcVar9 = pcVar8;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar8;
        pcVar8 = pcVar11 + 6;
        pcVar12 = pcVar13;
        if (cVar2 != '\0') break;
        pcVar13 = pcVar14 + 7;
        iVar4 = iVar3 + -7;
        pcVar9 = pcVar8;
        if (iVar4 == 0) goto LAB_004092ce;
        cVar2 = *pcVar8;
        pcVar8 = pcVar11 + 7;
        pcVar12 = pcVar13;
      } while (cVar2 == '\0');
      goto LAB_004091d9;
    }
LAB_004092ce:
    pcVar12 = pcVar13 + -iVar5;
    pcVar8 = pcVar9 + iVar6;
    iVar4 = iVar7 + -1;
    bVar1 = iVar7 < 1;
    iVar7 = iVar4;
    if (iVar4 == 0 || bVar1) {
      return;
    }
  } while( true );
}



/* Address: 004092e0 */

void FUN_004092e0(int param_1,int param_2,int param_3,int param_4,int param_5,int param_6,
                 int param_7,int param_8,int param_9)

{
  char cVar1;
  bool bVar2;
  char *pcVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  int iVar7;
  int iVar8;
  char *pcVar9;
  char *pcVar10;
  
  DAT_00411c64 = param_2 - param_4;
  pcVar10 = (char *)(((DAT_00411c60 + -1) - param_3) * DAT_00411c5c + param_4 + DAT_00411c58);
  pcVar9 = (char *)(((param_5 + -1 + param_7) * param_6 - param_8) + param_9);
  iVar5 = DAT_00411c5c + DAT_00411c64;
  iVar6 = param_6 + DAT_00411c64;
  iVar4 = DAT_00411c64;
  iVar7 = param_1 - param_3;
  do {
    cVar1 = *pcVar9;
    pcVar3 = pcVar10;
    while (pcVar9 = pcVar9 + 1, cVar1 == '\0') {
      iVar4 = iVar4 + -1;
      if (iVar4 == 0) goto LAB_00409357;
      pcVar3 = pcVar3 + 1;
      cVar1 = *pcVar9;
    }
    *pcVar3 = cVar1;
    pcVar10 = pcVar3 + 1;
    iVar4 = iVar4 + -1;
    if (iVar4 == 0) {
LAB_00409357:
      pcVar10 = pcVar3 + (1 - iVar5);
      pcVar9 = pcVar9 + -iVar6;
      iVar8 = iVar7 + -1;
      bVar2 = iVar7 < 1;
      iVar4 = DAT_00411c64;
      iVar7 = iVar8;
      if (iVar8 == 0 || bVar2) {
        return;
      }
    }
  } while( true );
}



/* Address: 00409368 */

void FUN_00409368(int param_1,int param_2,int param_3,int param_4,undefined4 param_5,int param_6,
                 int param_7,int param_8,int param_9)

{
  char cVar1;
  bool bVar2;
  char *pcVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  int iVar7;
  int iVar8;
  char *pcVar9;
  char *pcVar10;
  
  DAT_00411c64 = param_2 - param_4;
  pcVar10 = (char *)(((DAT_00411c60 + -1) - param_3) * DAT_00411c5c + param_4 + DAT_00411c58);
  pcVar9 = (char *)(-param_7 * param_6 + -1 + param_6 + param_8 + param_9);
  iVar5 = DAT_00411c5c + DAT_00411c64;
  iVar6 = param_6 + DAT_00411c64;
  iVar4 = DAT_00411c64;
  iVar7 = param_1 - param_3;
  do {
    cVar1 = *pcVar9;
    pcVar3 = pcVar10;
    while (pcVar9 = pcVar9 + -1, cVar1 == '\0') {
      iVar4 = iVar4 + -1;
      if (iVar4 == 0) goto LAB_004093e3;
      pcVar3 = pcVar3 + 1;
      cVar1 = *pcVar9;
    }
    *pcVar3 = cVar1;
    pcVar10 = pcVar3 + 1;
    iVar4 = iVar4 + -1;
    if (iVar4 == 0) {
LAB_004093e3:
      pcVar10 = pcVar3 + (1 - iVar5);
      pcVar9 = pcVar9 + iVar6;
      iVar8 = iVar7 + -1;
      bVar2 = iVar7 < 1;
      iVar4 = DAT_00411c64;
      iVar7 = iVar8;
      if (iVar8 == 0 || bVar2) {
        return;
      }
    }
  } while( true );
}



/* Address: 004093f4 */

void FUN_004093f4(int param_1,int param_2,int param_3,int param_4,int param_5,int param_6,
                 int param_7,int param_8,int param_9)

{
  char cVar1;
  bool bVar2;
  char *pcVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  int iVar7;
  int iVar8;
  char *pcVar9;
  char *pcVar10;
  
  DAT_00411c64 = param_2 - param_4;
  pcVar10 = (char *)(((DAT_00411c60 + -1) - param_3) * DAT_00411c5c + param_4 + DAT_00411c58);
  pcVar9 = (char *)((param_5 + -1 + param_7) * param_6 + -1 + param_6 + param_8 + param_9);
  iVar5 = DAT_00411c5c + DAT_00411c64;
  iVar6 = param_6 - DAT_00411c64;
  iVar4 = DAT_00411c64;
  iVar7 = param_1 - param_3;
  do {
    cVar1 = *pcVar9;
    pcVar3 = pcVar10;
    while (pcVar9 = pcVar9 + -1, cVar1 == '\0') {
      iVar4 = iVar4 + -1;
      if (iVar4 == 0) goto LAB_0040946f;
      pcVar3 = pcVar3 + 1;
      cVar1 = *pcVar9;
    }
    *pcVar3 = cVar1;
    pcVar10 = pcVar3 + 1;
    iVar4 = iVar4 + -1;
    if (iVar4 == 0) {
LAB_0040946f:
      pcVar10 = pcVar3 + (1 - iVar5);
      pcVar9 = pcVar9 + -iVar6;
      iVar8 = iVar7 + -1;
      bVar2 = iVar7 < 1;
      iVar4 = DAT_00411c64;
      iVar7 = iVar8;
      if (iVar8 == 0 || bVar2) {
        return;
      }
    }
  } while( true );
}



/* Address: 00409480 */

void FUN_00409480(undefined1 param_1)

{
  undefined4 in_EAX;
  uint uVar1;
  uint uVar2;
  uint *puVar3;
  
  uVar1 = CONCAT22((short)((uint)in_EAX >> 0x10),CONCAT11(param_1,param_1));
  puVar3 = DAT_00411c58;
  for (uVar2 = (uint)(DAT_00411c5c * DAT_00411c60) >> 2; uVar2 != 0; uVar2 = uVar2 - 1) {
    *puVar3 = uVar1 | uVar1 << 0x10;
    puVar3 = puVar3 + 1;
  }
  return;
}



/* Address: 004095ec */

int FUN_004095ec(void)

{
  int iVar1;
  int in_stack_00000014;
  int in_stack_00000018;
  
  iVar1 = (*(code *)(&PTR_LAB_004105d4)[in_stack_00000014])();
  return iVar1 - in_stack_00000018;
}



/* Address: 00409630 */

void FUN_00409630(int param_1,byte *param_2,byte *param_3)

{
  undefined2 uVar1;
  byte bVar2;
  byte bVar3;
  char cVar4;
  byte bVar5;
  
LAB_0040967d:
  do {
    bVar3 = *param_3;
    param_3 = param_3 + 1;
    cVar4 = '\b';
    param_1 = param_1 + -1;
    if (param_1 < 0) {
      return;
    }
    do {
      while ((bool)(bVar3 & 1)) {
        param_1 = param_1 + -1;
        bVar2 = *param_3;
        param_3 = param_3 + 1;
        *param_2 = bVar2;
        param_2 = param_2 + 1;
        cVar4 = cVar4 + -1;
        bVar3 = bVar3 >> 1;
        if (cVar4 == '\0') goto LAB_0040967d;
      }
      param_1 = param_1 + -2;
      if (param_1 < 0) {
        return;
      }
      uVar1 = *(undefined2 *)param_3;
      param_3 = param_3 + 2;
      bVar5 = (byte)((ushort)uVar1 >> 8);
      bVar2 = (bVar5 & 0xf) + 2;
      do {
        *param_2 = param_2[-(uint)CONCAT11(bVar5 >> 4,(char)uVar1)];
        param_2 = param_2 + 1;
        bVar2 = bVar2 - 1;
      } while (-1 < (short)((ushort)bVar2 << 8));
      cVar4 = cVar4 + -1;
      bVar3 = bVar3 >> 1;
    } while (cVar4 != '\0');
  } while( true );
}



/* Address: 0040968c */

void FUN_0040968c(char param_1,int param_2,int param_3,int param_4,int param_5)

{
  FUN_0040ba8c(param_1,DAT_00411c60,DAT_00411c5c,param_2 - param_4,param_3 - param_5,param_4,param_5
               ,DAT_00411c58);
  return;
}



/* Address: 004096c4 */

void FUN_004096c4(void)

{
  return;
}



/* Address: 004096c8 */

void FUN_004096c8(void)

{
  LPWAVEHDR pwh;
  
  pwh = (LPWAVEHDR)&DAT_00411c70;
  do {
    waveOutUnprepareHeader(DAT_00411c68,pwh,0x20);
    pwh = pwh + 1;
  } while (pwh < (LPWAVEHDR)&DAT_00411d70);
  waveOutClose(DAT_00411c68);
  return;
}



/* Address: 004096f8 */

undefined4 FUN_004096f8(DWORD_PTR *param_1)

{
  MMRESULT MVar1;
  byte *pbVar2;
  LPWAVEHDR pwh;
  WAVEFORMATEX local_18;
  DWORD_PTR *pDStack_4;
  
  local_18.wFormatTag = 1;
  local_18.nChannels = 1;
  local_18.nAvgBytesPerSec = 0x2b11;
  local_18.nSamplesPerSec = 0x2b11;
  local_18.nBlockAlign = 1;
  local_18.wBitsPerSample = 8;
  local_18.cbSize = 0;
  MVar1 = waveOutOpen(&DAT_00411c68,0xffffffff,&local_18,*param_1,0,0x10000);
  if (MVar1 != 0) {
    sndPlaySoundA((LPCSTR)0x0,0);
    MVar1 = waveOutOpen(&DAT_00411c68,0xffffffff,&local_18,*param_1,0,0x10000);
  }
  if (MVar1 == 0) {
    MVar1 = waveOutGetID(DAT_00411c68,&DAT_00411c6c);
    if (MVar1 == 0) {
      pbVar2 = &DAT_00411c80;
      pwh = (LPWAVEHDR)&DAT_00411c70;
      pDStack_4 = param_1 + 0x1000;
      do {
        pbVar2[0] = 1;
        pbVar2[1] = 0;
        pbVar2[2] = 0;
        pbVar2[3] = 0;
        *(DWORD_PTR **)(pbVar2 + -0x10) = param_1;
        pbVar2[-0xffffffff0000000c] = 0;
        pbVar2[-0xffffffff0000000b] = 8;
        pbVar2[-0xffffffff0000000a] = 0;
        pbVar2[-0xffffffff00000009] = 0;
        MVar1 = waveOutPrepareHeader(DAT_00411c68,pwh,0x20);
        *pbVar2 = *pbVar2 | 1;
        if (MVar1 != 0) break;
        pbVar2 = pbVar2 + 0x20;
        pwh = pwh + 1;
        param_1 = param_1 + 0x200;
      } while (param_1 < pDStack_4);
      if (MVar1 == 0) {
        return 1;
      }
    }
    FUN_004096c8();
  }
  return 0;
}



/* Address: 00409818 */

uint FUN_00409818(void)

{
  byte *pbVar1;
  
  pbVar1 = &DAT_00411c80;
  do {
    if ((*pbVar1 & 1) != 0) {
      return (uint)(pbVar1 + -0x411c80) >> 5;
    }
    pbVar1 = pbVar1 + 0x20;
  } while (pbVar1 < &DAT_00411d80);
  waveOutReset(DAT_00411c68);
  return 0;
}



/* Address: 0040992c */

void FUN_0040992c(void)

{
  if (DAT_00411d78 != 0) {
    timeKillEvent(DAT_00411d78);
    DAT_00411d78 = 0;
  }
  midiOutReset(DAT_00411d70);
  return;
}



/* Address: 00409958 */

uint FUN_00409958(void)

{
  byte bVar1;
  byte *pbVar2;
  uint uVar3;
  
  pbVar2 = DAT_00411d90;
  bVar1 = *DAT_00411d90;
  uVar3 = (uint)bVar1;
  DAT_00411d90 = DAT_00411d90 + 1;
  if ((bVar1 & 0x80) != 0) {
    uVar3 = bVar1 & 0x7f | (uint)*DAT_00411d90 << 7;
    DAT_00411d90 = pbVar2 + 2;
  }
  return uVar3;
}



/* Address: 0040998c */

void FUN_0040998c(void)

{
  byte *pbVar1;
  byte bVar2;
  DWORD DVar3;
  uint uVar4;
  undefined4 local_4;
  
  do {
    local_4 = 0;
    bVar2 = *DAT_00411d90;
    if (0x7f < bVar2) {
      DAT_00411d90 = DAT_00411d90 + 1;
      DAT_00411d94 = bVar2;
    }
    pbVar1 = DAT_00411d90;
    if ((DAT_00411d94 == 0xf0) || (DAT_00411d94 == 0xf7)) {
      uVar4 = FUN_00409958();
      DAT_00411d90 = DAT_00411d90 + uVar4;
    }
    else {
      if (DAT_00411d94 == 0xff) {
        if (DAT_00411d84 != 0xffff) {
          DAT_00411d88 = DAT_00411d84;
          DAT_00411d90 = (byte *)DAT_00411d8c;
          return;
        }
        FUN_0040992c();
        return;
      }
      uVar4 = (uint)CONCAT11(*DAT_00411d90,DAT_00411d94);
      DAT_00411d90 = DAT_00411d90 + 1;
      local_4 = 0x100;
      bVar2 = DAT_00411d94 & 0xf0;
      if ((bVar2 == 0x80) || (bVar2 == 0x90)) {
        local_4 = (uint)(byte)(1 - (DAT_00411d95 == '\0')) << 8;
LAB_00409a55:
        bVar2 = *DAT_00411d90;
        DAT_00411d90 = pbVar1 + 2;
        uVar4 = (bVar2 & 0x7f) << 0x10 | uVar4;
        local_4 = CONCAT31(local_4._1_3_,bVar2) & 0xffffff80;
      }
      else if ((bVar2 != 0xc0) && (bVar2 != 0xd0)) goto LAB_00409a55;
      if (local_4._1_1_ != '\0') {
        midiOutShortMsg(DAT_00411d70,uVar4);
      }
    }
    if (((char)local_4 == '\0') && (uVar4 = FUN_00409958(), uVar4 != 0)) {
      DAT_00411d7c = DAT_00411d7c + uVar4 * 10;
      DVar3 = timeGetTime();
      if (DVar3 < DAT_00411d7c) {
        return;
      }
    }
  } while( true );
}



/* Address: 00409b1c */

void FUN_00409b1c(void)

{
  FUN_0040992c();
  if (DAT_00411d98 != 0) {
    timeEndPeriod(DAT_00411d98);
    DAT_00411d98 = 0;
  }
  if (DAT_00411d70 != (HMIDIOUT)0x0) {
    midiOutClose(DAT_00411d70);
    DAT_00411d70 = (HMIDIOUT)0x0;
  }
  return;
}



/* Address: 00409b60 */

undefined4 FUN_00409b60(void)

{
  MMRESULT MVar1;
  uint uVar2;
  timecaps_tag tStack_8;
  
  MVar1 = midiOutOpen(&DAT_00411d70,0xffffffff,0,0,0);
  if (MVar1 == 0) {
    DAT_00411d98 = 0;
    DAT_00411d95 = 1;
    MVar1 = midiOutGetID(DAT_00411d70,&DAT_00411d74);
    if ((MVar1 == 0) && (MVar1 = timeGetDevCaps(&tStack_8,8), MVar1 == 0)) {
      uVar2 = tStack_8.wPeriodMin;
      if (tStack_8.wPeriodMin < 0xb) {
        uVar2 = 10;
      }
      if ((uVar2 < tStack_8.wPeriodMax) &&
         (tStack_8.wPeriodMax = tStack_8.wPeriodMin, tStack_8.wPeriodMin < 0xb)) {
        tStack_8.wPeriodMax = 10;
      }
      DAT_00411d98 = tStack_8.wPeriodMax;
      MVar1 = timeBeginPeriod(tStack_8.wPeriodMax);
      if (MVar1 == 0) {
        return 1;
      }
    }
    FUN_00409b1c();
  }
  return 0;
}



/* Address: 00409d04 */

void FUN_00409d04(void)

{
  return;
}



/* Address: 00409d08 */

undefined1 FUN_00409d08(void)

{
  return 1;
}



/* Address: 00409d0c */

undefined1 FUN_00409d0c(void)

{
  return 1;
}



/* Address: 00409d14 */

undefined1 FUN_00409d14(void)

{
  return 1;
}



/* Address: 00409d1c */

void FUN_00409d1c(void)

{
  if (DAT_00411da0 != (code *)0x0) {
    (*DAT_00411da0)(0,0x201,0);
  }
  if (DAT_00411da8 != (code *)0x0) {
    (*DAT_00411da8)(DAT_00416368);
  }
  if (DAT_00411dac != (HMODULE)0x0) {
    FreeLibrary(DAT_00411dac);
  }
  if (DAT_00411d9c != (HGLOBAL)0x0) {
    GlobalFree(DAT_00411d9c);
    DAT_00411d9c = (HGLOBAL)0x0;
  }
  return;
}



/* Address: 00409e6c */

undefined1 * FUN_00409e6c(void)

{
  int iVar1;
  char *pcVar2;
  
  DAT_00411dac = LoadLibraryA(s_KERNEL32_DLL_00410634);
  if (DAT_00411dac != (HMODULE)0x0) {
    DAT_00411da4 = GetProcAddress(DAT_00411dac,s_UTRegister_00410629);
    if (DAT_00411da4 != (FARPROC)0x0) {
      DAT_00411da8 = GetProcAddress(DAT_00411dac,s_UTUnRegister_0041061c);
      if (DAT_00411da8 != (FARPROC)0x0) {
        pcVar2 = s_GMW_DLL_00410614;
        iVar1 = (*DAT_00411da4)(DAT_00416368,s_GMW_DLL_00410614,0,2,&DAT_00411da0,0,0);
        if (iVar1 != 0) {
          iVar1 = (*DAT_00411da0)(pcVar2,0x200,0);
          if (iVar1 != 0) {
            DAT_00411d9c = GlobalAlloc(0,0xf000);
            if (DAT_00411d9c != (HGLOBAL)0x0) {
              PTR_FUN_004105e4 = &LAB_00409d80;
              PTR_FUN_004105ec = &LAB_00409d90;
              PTR_FUN_004105f4 = &LAB_00409da0;
              PTR_FUN_00410608 = &LAB_00409de0;
              PTR_FUN_004105fc = &LAB_00409db0;
              PTR_FUN_00410600 = &LAB_00409dc0;
              PTR_FUN_00410604 = &LAB_00409dd0;
              PTR_FUN_004105e8 = &LAB_00409df4;
              PTR_FUN_004105f0 = &LAB_00409e30;
              PTR_FUN_004105f8 = &LAB_00409e40;
              PTR_FUN_0041060c = &LAB_00409e50;
              PTR_FUN_00410610 = FUN_00409d1c;
              return &LAB_00409d01;
            }
          }
          FUN_00409d1c();
        }
      }
    }
  }
  return (undefined1 *)0x0;
}



/* Address: 00409f98 */

void FUN_00409f98(void)

{
  FUN_004096c8();
  FUN_00409b1c();
  return;
}



/* Address: 00409fa4 */

undefined4 FUN_00409fa4(DWORD_PTR *param_1)

{
  uint uVar1;
  uint uVar2;
  
  uVar1 = FUN_004096f8(param_1);
  uVar1 = uVar1 & 0xff;
  uVar2 = FUN_00409b60();
  if (uVar1 != 0 || (uVar2 & 0xff) != 0) {
    PTR_FUN_004105e4 = &LAB_00409848;
    PTR_FUN_004105ec = &LAB_00409870;
    PTR_FUN_004105f4 = &LAB_004098ac;
    PTR_FUN_00410608 = &LAB_004098dc;
    PTR_FUN_004105fc = FUN_00409818;
    PTR_FUN_00410600 = &LAB_00409884;
    PTR_FUN_00410604 = &LAB_00409898;
    PTR_FUN_004105e8 = &LAB_00409bfc;
    PTR_FUN_004105f0 = &LAB_00409c74;
    PTR_FUN_004105f8 = &LAB_00409c7c;
    PTR_FUN_0041060c = &DAT_00409cac;
    PTR_FUN_00410610 = FUN_00409f98;
    return 0x409f01;
  }
  FUN_00409f98();
  return 0;
}



/* Address: 0040a048 */

void FUN_0040a048(uint param_1,undefined4 *param_2,undefined4 *param_3)

{
  uint uVar1;
  int iVar2;
  
  for (uVar1 = param_1 >> 2; uVar1 != 0; uVar1 = uVar1 - 1) {
    *param_3 = *param_2;
    param_2 = param_2 + 1;
    param_3 = param_3 + 1;
  }
  iVar2 = 0x200 - (param_1 >> 2);
  if (iVar2 != 0) {
    for (; iVar2 != 0; iVar2 = iVar2 + -1) {
      *param_3 = 0x80808080;
      param_3 = param_3 + 1;
    }
  }
  return;
}



/* Address: 0040a074 */

void FUN_0040a074(uint param_1,undefined4 *param_2,uint *param_3)

{
  char cVar1;
  char cVar2;
  byte bVar3;
  uint3 uVar7;
  int iVar4;
  undefined2 uVar8;
  undefined4 uVar5;
  uint uVar6;
  uint uVar9;
  char cVar10;
  char cVar11;
  
  uVar9 = param_1 >> 2;
  do {
    uVar5 = *param_2;
    uVar6 = *param_3;
    cVar1 = (char)uVar5 + -0x80;
    cVar10 = (char)uVar6 + -0x80;
    cVar11 = (char)(uVar6 >> 8) + -0x80;
    uVar7 = (uint3)(CONCAT22((short)((uint)uVar5 >> 0x10),
                             CONCAT11((char)((uint)uVar5 >> 8) + -0x80,cVar1)) >> 8);
    cVar2 = cVar1 + cVar10;
    if (SCARRY1(cVar1,cVar10)) {
      if (cVar2 < '\0') {
        iVar4 = CONCAT31(uVar7,0xf0);
      }
      else {
        iVar4 = (uint)uVar7 << 8;
      }
    }
    else {
      iVar4 = CONCAT31(uVar7,cVar2 + -0x80);
    }
    cVar2 = (char)((uint)iVar4 >> 8);
    uVar8 = (undefined2)((uint)iVar4 >> 0x10);
    bVar3 = (byte)iVar4;
    cVar1 = cVar2 + cVar11;
    if (SCARRY1(cVar2,cVar11)) {
      if (cVar1 < '\0') {
        uVar5 = CONCAT22(uVar8,CONCAT11(0xf0,bVar3));
      }
      else {
        uVar5 = CONCAT22(uVar8,(ushort)bVar3);
      }
    }
    else {
      uVar5 = CONCAT22(uVar8,CONCAT11(cVar1 + -0x80,bVar3));
    }
    cVar1 = (char)((uint)uVar5 >> 0x10) + -0x80;
    cVar10 = (char)(uVar6 >> 0x10) + -0x80;
    cVar11 = (char)(uVar6 >> 0x18) + -0x80;
    uVar7 = (uint3)(CONCAT22((short)uVar5,CONCAT11((char)((uint)uVar5 >> 0x18) + -0x80,cVar1)) >> 8)
    ;
    cVar2 = cVar1 + cVar10;
    if (SCARRY1(cVar1,cVar10)) {
      if (cVar2 < '\0') {
        iVar4 = CONCAT31(uVar7,0xf0);
      }
      else {
        iVar4 = (uint)uVar7 << 8;
      }
    }
    else {
      iVar4 = CONCAT31(uVar7,cVar2 + -0x80);
    }
    cVar2 = (char)((uint)iVar4 >> 8);
    uVar8 = (undefined2)((uint)iVar4 >> 0x10);
    bVar3 = (byte)iVar4;
    cVar1 = cVar2 + cVar11;
    if (SCARRY1(cVar2,cVar11)) {
      if (cVar1 < '\0') {
        uVar6 = CONCAT22(uVar8,CONCAT11(0xf0,bVar3));
      }
      else {
        uVar6 = CONCAT22(uVar8,(ushort)bVar3);
      }
    }
    else {
      uVar6 = CONCAT22(uVar8,CONCAT11(cVar1 + -0x80,bVar3));
    }
    uVar9 = uVar9 - 1;
    *param_3 = uVar6 << 0x10 | uVar6 >> 0x10;
    param_2 = param_2 + 1;
    param_3 = param_3 + 1;
  } while (uVar9 != 0);
  return;
}



/* Address: 0040a0f4 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_0040a0f4(undefined1 *param_1,undefined4 param_2,byte param_3)

{
  undefined1 uVar1;
  DWORD DVar2;
  code *pcVar3;
  undefined4 *puVar4;
  undefined4 *puVar5;
  undefined1 *unaff_retaddr;
  
  DVar2 = GetVersion();
  _DAT_00411e30 = DAT_0041636c;
  if (((DVar2 & 0x80000000) == 0) || (3 < (byte)DVar2)) {
    pcVar3 = FUN_00409fa4;
  }
  else {
    pcVar3 = FUN_00409e6c;
  }
  puVar4 = (undefined4 *)(*pcVar3)(&DAT_00411e30);
  if ((char)puVar4 != '\0') {
    uVar1 = (*(code *)PTR_FUN_004105f4)();
    *param_1 = uVar1;
    uVar1 = (*(code *)PTR_FUN_004105f8)();
    *unaff_retaddr = uVar1;
    DAT_00415e30 = (uint)param_3;
    DAT_00415e38 = 0;
    DAT_00415e34 = 0;
    puVar5 = &DAT_00411db0;
    do {
      *puVar5 = 0;
      puVar5 = puVar5 + 4;
      puVar4 = &DAT_00411db0 + DAT_00415e30 * 4;
    } while (puVar5 < puVar4);
  }
  return CONCAT31((int3)((uint)puVar4 >> 8),1);
}



/* Address: 0040a188 */

void FUN_0040a188(undefined4 param_1,undefined4 param_2,undefined4 param_3)

{
  (*(code *)PTR_FUN_004105e8)(param_3);
  return;
}



/* Address: 0040a198 */

undefined4 FUN_0040a198(byte param_1,undefined1 param_2,undefined1 param_3,uint *param_4)

{
  uint *puVar1;
  undefined3 uVar2;
  undefined3 extraout_var;
  int iVar3;
  uint uVar4;
  
  uVar4 = DAT_00415e34;
  do {
    if ((&DAT_00411db0)[uVar4 * 4] == 0) break;
    uVar4 = uVar4 + 1;
    if (DAT_00415e30 <= uVar4) {
      uVar4 = 0;
    }
  } while (uVar4 != DAT_00415e34);
  iVar3 = uVar4 * 0x10;
  DAT_00415e34 = uVar4;
  (&DAT_00411db8)[uVar4 * 4] = *param_4 & 0xfffffffc;
  puVar1 = param_4 + 1;
  (&DAT_00411db0)[uVar4 * 4] = puVar1;
  (&DAT_00411db4)[uVar4 * 4] = puVar1;
  uVar2 = (undefined3)((uint)puVar1 >> 8);
  (&DAT_00411dbd)[iVar3] = param_2;
  (&DAT_00411dbc)[iVar3] = param_3;
  (&DAT_00411dbe)[iVar3] = param_1;
  if ((param_1 & 0x80) == 0) {
    if (DAT_00415e38 == 0) {
      iVar3 = 2;
    }
    else {
      iVar3 = 0;
    }
  }
  else {
    iVar3 = 1;
  }
  if (iVar3 == 2) {
    FUN_0040a290();
  }
  else if (iVar3 != 1) goto LAB_0040a23a;
  FUN_0040a290();
  uVar2 = extraout_var;
LAB_0040a23a:
  return CONCAT31(uVar2,1);
}



/* Address: 0040a240 */

void FUN_0040a240(undefined4 param_1)

{
  (*(code *)PTR_FUN_0041060c)(param_1);
  return;
}



/* Address: 0040a250 */

void FUN_0040a250(undefined4 param_1)

{
  (*(code *)PTR_FUN_00410608)(param_1);
  return;
}



/* Address: 0040a260 */

void FUN_0040a260(void)

{
  (*(code *)PTR_FUN_004105f0)();
  return;
}



/* Address: 0040a268 */

void FUN_0040a268(void)

{
  undefined4 *puVar1;
  
  puVar1 = &DAT_00411db0;
  do {
    *puVar1 = 0;
    puVar1 = puVar1 + 4;
  } while (puVar1 < &DAT_00411db0 + DAT_00415e30 * 4);
  (*(code *)PTR_FUN_004105ec)();
  return;
}



/* Address: 0040a290 */

void FUN_0040a290(void)

{
  int *piVar1;
  uint uVar2;
  code *pcVar3;
  int *piVar4;
  uint uVar5;
  int iVar6;
  
  iVar6 = 0;
  uVar2 = (*(code *)PTR_FUN_004105fc)();
  piVar4 = &DAT_00411db0;
  do {
    if (*piVar4 != 0) {
      uVar5 = piVar4[2];
      if (0x800 < uVar5) {
        uVar5 = 0x800;
      }
      if (iVar6 == 0) {
        pcVar3 = FUN_0040a048;
      }
      else {
        pcVar3 = FUN_0040a074;
      }
      (*pcVar3)(uVar5,piVar4[1],&DAT_00411e30 + (uVar2 & 0xff) * 0x800);
      piVar1 = piVar4 + 2;
      *piVar1 = *piVar1 - uVar5;
      if (*piVar1 == 0) {
        *piVar4 = 0;
      }
      else {
        piVar4[1] = piVar4[1] + uVar5;
      }
      iVar6 = iVar6 + 1;
    }
    piVar4 = piVar4 + 4;
  } while (piVar4 < &DAT_00411db0 + DAT_00415e30 * 4);
  DAT_00415e38 = iVar6;
  if (iVar6 != 0) {
    (*(code *)PTR_FUN_004105e4)();
  }
  return;
}



/* Address: 0040a318 */

void FUN_0040a318(void)

{
  (*(code *)PTR_FUN_00410600)();
  return;
}



/* Address: 0040a320 */

void FUN_0040a320(void)

{
  (*(code *)PTR_FUN_00410604)();
  return;
}



/* Address: 0040a328 */

void FUN_0040a328(void)

{
  (*(code *)PTR_FUN_00410610)();
  PTR_FUN_004105e8 = FUN_00409d14;
  PTR_FUN_00410604 = FUN_00409d08;
  PTR_FUN_00410600 = FUN_00409d08;
  PTR_FUN_004105fc = FUN_00409d08;
  PTR_FUN_004105f8 = FUN_00409d08;
  PTR_FUN_004105f4 = FUN_00409d08;
  PTR_FUN_004105f0 = FUN_00409d08;
  PTR_FUN_004105ec = FUN_00409d08;
  PTR_FUN_004105e4 = FUN_00409d08;
  PTR_FUN_0041060c = FUN_00409d0c;
  PTR_FUN_00410608 = FUN_00409d0c;
  PTR_FUN_00410610 = FUN_00409d04;
  return;
}



/* Address: 0040a380 */

void FUN_0040a380(LPCSTR param_1,LPCSTR param_2)

{
  MessageBeep(0x10);
  MessageBoxA(DAT_0041636c,param_1,param_2,0x10);
  return;
}



/* Address: 0040a3a4 */

void FUN_0040a3a4(LPCSTR param_1)

{
  FUN_0040a380(param_1,s_Game_Maker_Player_0041086c);
  return;
}



/* Address: 0040a3b8 */

void FUN_0040a3b8(void)

{
  if (DAT_00415e9c != (LPCSTR)0x0) {
    FUN_0040a3a4(DAT_00415e9c);
    DAT_00415e9c = (LPCSTR)0x0;
  }
  return;
}



/* Address: 0040a3d8 */

void FUN_0040a3d8(uint *param_1)

{
  uint *puVar1;
  char *pcVar2;
  char cVar3;
  
  cVar3 = '.';
  puVar1 = FUN_0040c5d8((uint *)&DAT_00415ee4,param_1);
  pcVar2 = FUN_0040d2fc(puVar1,cVar3);
  *pcVar2 = '\0';
  FUN_0040dc60(&DAT_00415ee4,&DAT_0041087e);
  return;
}



/* Address: 0040a40c */

void FUN_0040a40c(void)

{
  if (DAT_0041636c != (HWND)0x0) {
    WinHelpA(DAT_0041636c,&DAT_00415ee4,2,0);
    DestroyWindow(DAT_0041636c);
  }
  return;
}



/* Address: 0040a438 */

void FUN_0040a438(void)

{
  undefined4 extraout_ECX;
  undefined4 extraout_EDX;
  
  DAT_0041636c = 0;
  FUN_00404498();
  FUN_0040bd64(extraout_ECX,extraout_EDX);
  if (DAT_00415e8c != (HGDIOBJ)0x0) {
    DeleteObject(DAT_00415e8c);
    DAT_00415e8c = (HGDIOBJ)0x0;
  }
  if (DAT_00415fec != (HDC)0x0) {
    DeleteDC(DAT_00415fec);
  }
  if (DAT_00415fe4 != (HGDIOBJ)0x0) {
    DeleteObject(DAT_00415fe4);
  }
  if (DAT_00415e90 != (HGLOBAL)0x0) {
    GlobalFree(DAT_00415e90);
    DAT_00415e90 = (HGLOBAL)0x0;
    DAT_00415e94 = 0;
  }
  return;
}



/* Address: 0040a4b4 */

HCURSOR FUN_0040a4b4(void)

{
  HCURSOR pHVar1;
  
  if (*(char *)(DAT_00415e7c + 0x25) == '\0') {
    pHVar1 = (HCURSOR)0x0;
  }
  else {
    pHVar1 = LoadCursorA((HINSTANCE)0x0,(LPCSTR)0x7f00);
  }
  return pHVar1;
}



/* Address: 0040a4d4 */

undefined4 FUN_0040a4d4(MCIERROR param_1)

{
  undefined4 in_EAX;
  CHAR local_100 [256];
  
  if (param_1 != 0) {
    mciGetErrorStringA(param_1,local_100,0x100);
    FUN_0040a3a4(local_100);
    return 0;
  }
  return CONCAT31((int3)((uint)in_EAX >> 8),1);
}



/* Address: 0040a51c */

int FUN_0040a51c(DWORD_PTR param_1,DWORD_PTR param_2,UINT param_3)

{
  MCIERROR MVar1;
  undefined4 uVar2;
  int iVar3;
  
  if (DAT_00416040 == 0) {
    iVar3 = 0;
  }
  else {
    MVar1 = mciSendCommandA(DAT_00416040,param_3,param_2,param_1);
    uVar2 = FUN_0040a4d4(MVar1);
    iVar3 = (int)(char)uVar2;
  }
  return iVar3;
}



/* Address: 0040a550 */

void FUN_0040a550(DWORD_PTR param_1,UINT param_2)

{
  undefined1 local_4 [4];
  
  if (DAT_00416040 != 0) {
    FUN_0040a51c((DWORD_PTR)local_4,param_1,param_2);
    return;
  }
  return;
}



/* Address: 0040a578 */

void FUN_0040a578(void)

{
  uint local_c [3];
  
  local_c[0] = (uint)(ushort)DAT_0041636c;
  FUN_0040a51c((DWORD_PTR)local_c,1,0x806);
  return;
}



/* Address: 0040a59c */

LRESULT FUN_0040a59c(HWND param_1,UINT param_2,HWND param_3,uint param_4)

{
  LRESULT LVar1;
  int iVar2;
  int nWidth;
  HCURSOR hCursor;
  UINT UVar3;
  tagPAINTSTRUCT local_40;
  
  if (param_2 == 0x47) {
    InvalidateRect(param_1,(RECT *)0x0,0);
    goto LAB_0040a632;
  }
  if (param_2 == 0x24) {
    iVar2 = DAT_00415eb0 + DAT_00415ec0;
    *(int *)(param_4 + 0x20) = iVar2;
    *(int *)(param_4 + 8) = iVar2;
    iVar2 = DAT_00415eb4 + DAT_00415ec4;
    *(int *)(param_4 + 0x24) = iVar2;
    *(int *)(param_4 + 0xc) = iVar2;
    return 0;
  }
  if (param_2 == 5) {
    DAT_00415ea8 = param_4 & 0xffff;
    DAT_00415eac = param_4 >> 0x10;
    return 0;
  }
  if (param_2 == 0x210) {
    if ((((uint)param_3 & 0xffff) != 0x201) && (((uint)param_3 & 0xffff) != 0x204)) {
      return 0;
    }
  }
  else if ((param_2 != 0x201) && (param_2 != 0x204)) {
    if (param_2 != 0x102) {
      if (param_2 == 0x20) {
        if ((short)param_4 == 1) {
          hCursor = LoadCursorA((HINSTANCE)0x0,(LPCSTR)0x7f00);
          SetCursor(hCursor);
          return 0;
        }
LAB_0040a632:
        LVar1 = DefWindowProcA(param_1,param_2,(WPARAM)param_3,param_4);
        return LVar1;
      }
      if (param_2 == 0x311) {
        if (param_3 == param_1) {
          return 0;
        }
      }
      else if (param_2 != 0x30f) {
        if (param_2 == 0xf) {
          BeginPaint(param_1,&local_40);
          if (DAT_0041604a._2_1_ != '\0') {
            if (DAT_0041604e == '\0') {
              DAT_0041604e = '\x01';
              iVar2 = DAT_00415ffc;
              if (DAT_00415ffc == 0) {
                iVar2 = 0xf0;
              }
              nWidth = DAT_00415ff8;
              if (DAT_00415ff8 == 0) {
                nWidth = 0x140;
              }
              MoveWindow(DAT_00416044,DAT_00415ff0,DAT_00415ff4,nWidth,iVar2,1);
            }
            FUN_0040968c((char)DAT_0041604a,local_40.rcPaint.bottom,local_40.rcPaint.right,
                         local_40.rcPaint.top,local_40.rcPaint.left);
            DAT_0041604a = DAT_0041604a & 0xffffff00;
          }
          EndPaint(param_1,&local_40);
          return 0;
        }
        if (param_2 == 0x3b9) {
          DAT_00416048 = 0;
          return 0;
        }
        if (param_2 == 0x10) {
          DAT_00416049 = 1;
          goto LAB_0040a6ad;
        }
        goto LAB_0040a632;
      }
      if (DAT_00415e8c == (HPALETTE)0x0) {
        return 0;
      }
      if ((param_2 == 0x30f) || (param_3 != DAT_00416044)) {
        DAT_0041604a._0_3_ = (uint3)(ushort)DAT_0041604a;
      }
      SendMessageA(DAT_00416044,param_2,(WPARAM)param_3,param_4);
      if ((DAT_0041604a._3_1_ != '\0') || (param_2 == 0x311)) {
        DAT_0041604a._0_3_ = CONCAT12(1,(ushort)DAT_0041604a);
      }
      SelectPalette(DAT_00415e88,DAT_00415e8c,(uint)(param_2 == 0x311));
      UVar3 = RealizePalette(DAT_00415e88);
      if (UVar3 == 0) {
        return 0;
      }
      InvalidateRect(param_1,(RECT *)0x0,0);
      return 1;
    }
    if ((param_3 != (HWND)0x20) && (param_3 != (HWND)0xd)) {
      return 0;
    }
  }
LAB_0040a6ad:
  FUN_0040a550(0,0x808);
  return 0;
}



/* Address: 0040a870 */

undefined4 FUN_0040a870(char param_1,char *param_2,char *param_3)

{
  undefined4 in_EAX;
  undefined3 uVar1;
  char *pcVar2;
  int iVar3;
  
  iVar3 = 0;
  pcVar2 = param_2;
  do {
    uVar1 = (undefined3)((uint)in_EAX >> 8);
    in_EAX = CONCAT31(uVar1,*pcVar2);
    if (*pcVar2 == param_1) {
      param_2[iVar3] = '\0';
      *param_3 = (char)iVar3 + 'A';
      param_3[1] = ':';
      param_3[2] = '\0';
      return CONCAT31(uVar1,1);
    }
    pcVar2 = pcVar2 + 1;
    iVar3 = iVar3 + 1;
  } while (iVar3 < 0x20);
  return 0;
}



/* Address: 0040a8b4 */

undefined4 FUN_0040a8b4(char *param_1,char *param_2)

{
  undefined4 uVar1;
  
  uVar1 = FUN_0040a870('\x03',param_1,param_2);
  if (((((char)uVar1 == '\0') && (uVar1 = FUN_0040a870('\x05',param_1,param_2), (char)uVar1 == '\0')
       ) && (uVar1 = FUN_0040a870('\x04',param_1,param_2), (char)uVar1 == '\0')) &&
     (uVar1 = FUN_0040a870('\x06',param_1,param_2), (char)uVar1 == '\0')) {
    return 0;
  }
  return 1;
}



/* Address: 0040a904 */

undefined4 FUN_0040a904(uint *param_1,uint *param_2,uint *param_3)

{
  UINT UVar1;
  uint *puVar2;
  char *pcVar3;
  DWORD DVar4;
  undefined4 uVar5;
  char cVar6;
  char acStack_28 [32];
  uint uStack_8;
  char *pcStack_4;
  
  uStack_8 = GetLogicalDrives();
  pcStack_4 = (char *)0x0;
  do {
    pcVar3 = acStack_28 + (int)pcStack_4;
    *pcVar3 = '\0';
    if ((uStack_8 & 1 << ((byte)pcStack_4 & 0x1f)) != 0) {
      DAT_00410884 = (byte)pcStack_4 + 0x41;
      UVar1 = GetDriveTypeA(&DAT_00410884);
      *pcVar3 = (char)UVar1;
    }
    pcStack_4 = (char *)((int)pcStack_4 + 1);
  } while (pcStack_4 < 0x20);
  cVar6 = '\\';
  puVar2 = FUN_0040c5d8(param_3,(uint *)&DAT_00415e3c);
  pcVar3 = FUN_0040d2fc(puVar2,cVar6);
  *pcVar3 = '\0';
  pcVar3 = FUN_0040c63c(param_3);
  pcStack_4 = FUN_0040c63c(param_1);
  do {
    if (pcStack_4 + (int)pcVar3 < (char *)0x48) {
      puVar2 = param_1;
      pcVar3 = FUN_0040dc60((char *)param_3,&DAT_00410892);
      puVar2 = (uint *)FUN_0040dc60(pcVar3,(char *)puVar2);
      FUN_0040c5d8(param_2,puVar2);
      FUN_0040dc60((char *)param_3,&DAT_0041088d);
      FUN_0040dc60((char *)param_2,&DAT_00410888);
      DVar4 = GetFileAttributesA((LPCSTR)param_3);
      if (DVar4 != 0xffffffff) {
        return CONCAT31((int3)(DVar4 >> 8),1);
      }
      pcVar3 = (char *)0x2;
    }
    uVar5 = FUN_0040a8b4(acStack_28,(char *)param_3);
  } while ((char)uVar5 != '\0');
  return 0;
}



/* Address: 0040aa10 */

bool FUN_0040aa10(uint *param_1,uint *param_2,uint *param_3)

{
  uint *puVar1;
  char *pcVar2;
  DWORD DVar3;
  char cVar4;
  uint local_40 [16];
  
  cVar4 = '\\';
  puVar1 = FUN_0040c5d8(local_40,(uint *)&DAT_00415e3c);
  pcVar2 = FUN_0040d2fc(puVar1,cVar4);
  pcVar2[1] = '\0';
  pcVar2 = FUN_0040d2fc(param_1,'\\');
  FUN_0040dc60((char *)local_40,pcVar2 + 1);
  pcVar2 = &DAT_0041088d;
  puVar1 = FUN_0040c5d8(param_3,local_40);
  FUN_0040dc60((char *)puVar1,pcVar2);
  pcVar2 = &DAT_00410888;
  puVar1 = FUN_0040c5d8(param_2,local_40);
  FUN_0040dc60((char *)puVar1,pcVar2);
  DVar3 = GetFileAttributesA((LPCSTR)param_3);
  return DVar3 != 0xffffffff;
}



/* Address: 0040aaa4 */

void FUN_0040aaa4(LPCSTR param_1)

{
  uint uVar1;
  DWORD extraout_ECX;
  DWORD extraout_ECX_00;
  DWORD extraout_ECX_01;
  undefined4 extraout_ECX_02;
  undefined4 extraout_ECX_03;
  undefined4 uVar2;
  char *extraout_EDX;
  char *extraout_EDX_00;
  undefined1 *puVar3;
  char *extraout_EDX_01;
  undefined4 extraout_EDX_02;
  undefined4 extraout_EDX_03;
  undefined4 uVar4;
  DWORD DVar5;
  int iVar6;
  undefined1 *puVar7;
  uint unaff_ESI;
  undefined8 uVar8;
  short local_738;
  int local_736;
  short local_732;
  short local_730;
  int local_72e;
  char local_728 [4];
  int local_724;
  int local_720;
  short local_71c;
  short local_71a;
  int local_718;
  UINT local_708;
  undefined1 local_700 [1024];
  BYTE local_300 [2];
  undefined1 local_2fe [766];
  
  uVar8 = FUN_0040d564(param_1,0x8000,unaff_ESI);
  uVar1 = (uint)uVar8;
  if (uVar1 != 0xffffffff) {
    FUN_0040da70(extraout_ECX,(char *)((ulonglong)uVar8 >> 0x20),uVar1,(char *)&local_738,0xe);
    FUN_0040da70(extraout_ECX_00,extraout_EDX,uVar1,local_728,0x28);
    if (local_708 == 0) {
      local_708 = 0x100;
    }
    DVar5 = local_708 * 4;
    FUN_0040da70((DWORD)local_700,extraout_EDX_00,uVar1,local_700,DVar5);
    iVar6 = DVar5 + 0x36;
    if ((((((local_738 == 0x4d42) && (*(int *)(DAT_00415e80 + 8) + iVar6 == local_736)) &&
          (local_732 == 0)) && ((local_730 == 0 && (local_72e == iVar6)))) &&
        ((local_71c == 1 && ((local_71a == 8 && (local_718 == 0)))))) &&
       ((*(int *)(DAT_00415e7c + 0xc) == local_724 && (*(int *)(DAT_00415e7c + 0x10) == local_720)))
       ) {
      if (local_708 != 0) {
        puVar7 = local_700;
        puVar3 = local_2fe;
        do {
          puVar3[-2] = puVar7[2];
          puVar3[-1] = puVar7[1];
          *puVar3 = *puVar7;
          puVar7 = puVar7 + 4;
          puVar3 = puVar3 + 3;
        } while (puVar7 < local_700 + local_708 * 4);
      }
      FUN_0040b780('\x01','\0',local_708,local_300);
      FUN_0040da70(extraout_ECX_01,extraout_EDX_01,uVar1,*(char **)(DAT_00415e80 + 0xc),
                   *(DWORD *)(DAT_00415e80 + 8));
      uVar2 = extraout_ECX_02;
      uVar4 = extraout_EDX_02;
    }
    else {
      FUN_0040a3a4(s_Invalid_movie_background_bitmap__00410894);
      uVar2 = extraout_ECX_03;
      uVar4 = extraout_EDX_03;
    }
    FUN_0040d764(uVar2,uVar4,uVar1);
  }
  return;
}



/* Address: 0040ac08 */

void FUN_0040ac08(void)

{
  bool bVar1;
  undefined4 uVar2;
  MCIERROR MVar3;
  uint local_c4 [18];
  uint local_7c [18];
  undefined1 local_34 [4];
  undefined4 uStack_30;
  undefined1 *local_28;
  undefined4 local_20;
  undefined4 local_1c;
  undefined1 auStack_18 [4];
  HWND pHStack_14;
  undefined4 uStack_10;
  
  DAT_0041604a._0_1_ = *(undefined1 *)(DAT_00415e7c + 0x26);
  if (*(char *)(DAT_00415e7c + 0x27) == '\x02') {
    bVar1 = FUN_0040aa10((uint *)&DAT_00416000,local_7c,local_c4);
    if ((!bVar1) &&
       (uVar2 = FUN_0040a904((uint *)&DAT_00416000,local_7c,local_c4), (char)uVar2 == '\0')) {
      return;
    }
    local_28 = (undefined1 *)local_c4;
    local_20 = 0x40000000;
    local_1c = DAT_0041636c;
    MVar3 = mciSendCommandA(0,0x803,0x30200,(DWORD_PTR)local_34);
    uVar2 = FUN_0040a4d4(MVar3);
    if ((char)uVar2 != '\0') {
      DAT_00416040 = uStack_30;
      uStack_10 = 0x4001;
      FUN_0040a51c((DWORD_PTR)auStack_18,0x100,0x814);
      DAT_00416044 = pHStack_14;
      MoveWindow(pHStack_14,DAT_00415ff0,DAT_00415ff4,0,0,0);
      FUN_0040aaa4((LPCSTR)local_7c);
    }
  }
  return;
}



/* Address: 0040ad04 */

void FUN_0040ad04(void)

{
  int iVar1;
  tagMSG local_1c;
  
  iVar1 = PeekMessageA(&local_1c,(HWND)0x0,0,0,1);
  while (iVar1 != 0) {
    if ((((local_1c.message != 0x100) && (local_1c.message != 0x201)) && (local_1c.message != 0x204)
        ) && (((local_1c.message != 0x202 && (local_1c.message != 0x205)) &&
              (local_1c.message != 0x200)))) {
      TranslateMessage(&local_1c);
      DispatchMessageA(&local_1c);
    }
    iVar1 = PeekMessageA(&local_1c,(HWND)0x0,0,0,1);
  }
  return;
}



/* Address: 0040ad7c */

undefined4
FUN_0040ad7c(undefined4 param_1,undefined4 param_2,undefined4 param_3,undefined4 param_4,
            char *param_5)

{
  char cVar1;
  DWORD DVar2;
  char *pcVar3;
  uint *puVar4;
  uint uVar5;
  BOOL BVar6;
  char *pcVar7;
  byte bStack_28;
  tagMSG tStack_24;
  tagPOINT tStack_8;
  
  DVar2 = GetVersion();
  if (((DVar2 & 0x80000000) == 0) || (bStack_28 = (byte)DVar2, 3 < bStack_28)) {
    DAT_0041604a._3_1_ = 0;
  }
  else {
    DAT_0041604a._3_1_ = 1;
  }
  DAT_0041604a._1_1_ = 1;
  DAT_00416040 = 0;
  DAT_00416049 = '\0';
  DAT_00416048 = '\0';
  DAT_0041604e = 0;
  DAT_0041604a._2_1_ = '\0';
  DAT_00415ff0 = param_4;
  DAT_00415ff4 = param_3;
  DAT_00415ff8 = param_2;
  DAT_00415ffc = param_1;
  pcVar3 = param_5;
  if (param_5 == (char *)0x0) {
    pcVar3 = s_intro_004108b5;
  }
  pcVar7 = &DAT_00410892;
  puVar4 = FUN_0040c5d8((uint *)&DAT_00416000,(uint *)(DAT_00415e7c + 0x38));
  pcVar7 = FUN_0040dc60((char *)puVar4,pcVar7);
  FUN_0040dc60(pcVar7,pcVar3);
  FUN_0040ac08();
  if (DAT_00416040 != 0) {
    cVar1 = FUN_0040a578();
    if (cVar1 != '\0') {
      if (param_5 == (char *)0x0) {
        ShowWindow(DAT_0041636c,1);
      }
      else {
        InvalidateRect(DAT_0041636c,(RECT *)0x0,0);
      }
      if (DAT_0041604a._2_1_ == '\0') {
        uVar5 = GetDeviceCaps(DAT_00415e88,0x26);
        if ((uVar5 & 0x100) == 0) {
          DAT_0041604a._2_1_ = '\x01';
        }
        else {
          PostMessageA(DAT_0041636c,0x30f,0,0);
        }
      }
      DAT_00416048 = '\x01';
      do {
        GetMessageA(&tStack_24,(HWND)0x0,0,0);
        TranslateMessage(&tStack_24);
        DispatchMessageA(&tStack_24);
      } while (DAT_00416048 != '\0');
    }
    FUN_0040a550(0,0x804);
    DestroyWindow(DAT_00416044);
    DAT_00416044 = (HWND)0x0;
    DAT_0041604a._1_1_ = 0;
    if (DAT_00416049 == '\0') {
      FUN_004043f0();
      FUN_0040ad04();
      GetCursorPos(&tStack_8);
      BVar6 = SetCursorPos(tStack_8.x,tStack_8.y);
    }
    else {
      BVar6 = PostMessageA(DAT_0041636c,0x10,0,0);
    }
    return CONCAT31((int3)((uint)BVar6 >> 8),1);
  }
  DAT_0041604a._1_1_ = 0;
  return 0;
}



/* Address: 0040af64 */

LRESULT FUN_0040af64(HWND param_1,UINT param_2,HWND param_3,uint param_4)

{
  SHORT SVar1;
  LRESULT LVar2;
  BOOL BVar3;
  UINT UVar4;
  HCURSOR hCursor;
  int iVar5;
  tagRECT local_10;
  
  if (DAT_0041604a._1_1_ != '\0') {
    LVar2 = FUN_0040a59c(param_1,param_2,param_3,param_4);
    return LVar2;
  }
  if (param_2 == 0xf) {
    BVar3 = GetUpdateRect(param_1,&local_10,0);
    if (BVar3 != 0) {
      FUN_0040426c(local_10.bottom,local_10.right,local_10.top,local_10.left);
LAB_0040b066:
      LVar2 = DefWindowProcA(param_1,param_2,(WPARAM)param_3,param_4);
      return LVar2;
    }
  }
  else {
    if (param_2 == 0x311) {
      if (param_3 == param_1) {
        return 0;
      }
    }
    else if (param_2 != 0x30f) {
      if (param_2 == 0x102) {
        if ((*(byte *)(DAT_00415e84 + 4) & 1) == 0) {
          return 0;
        }
        DAT_00415ed8._2_1_ = (char)param_3;
        return 0;
      }
      if (param_2 == 0x100) {
        if ((*(byte *)(DAT_00415e84 + 4) & 1) == 0) {
          return 0;
        }
        if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x28) == param_3) {
          DAT_00415ed8._1_1_ = 0;
        }
        else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x29) == param_3) {
          DAT_00415ed8._1_1_ = 1;
        }
        else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x2a) == param_3) {
          DAT_00415ed8._1_1_ = 2;
        }
        else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x2b) == param_3) {
          DAT_00415ed8._1_1_ = 3;
        }
        else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x2c) == param_3) {
          DAT_00415ed8._1_1_ = 4;
        }
        else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x2d) == param_3) {
          DAT_00415ed8._1_1_ = 5;
        }
        else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x2e) == param_3) {
          DAT_00415ed8._1_1_ = 6;
        }
        else {
          if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x2f) != param_3) {
            if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x30) == param_3) {
              DAT_00415ed8._0_1_ = 0x82;
            }
            else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x31) == param_3) {
              DAT_00415ed8._0_1_ = 0x83;
            }
            else if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x32) == param_3) {
              DAT_00415ed8._0_1_ = 0x8f;
            }
            else {
              if ((HWND)(uint)*(byte *)(DAT_00415e84 + 0x33) != param_3) {
                return 0;
              }
              DAT_00415ed8._0_1_ = 0x84;
            }
            DAT_00415ed8._2_1_ = (char)param_3;
            return 0;
          }
          DAT_00415ed8._1_1_ = 7;
        }
        if ((DAT_00415ed8._1_1_ < 8) && (SVar1 = GetAsyncKeyState(0x10), SVar1 != 0)) {
          DAT_00415ed8._1_1_ = DAT_00415ed8._1_1_ | 8;
        }
        DAT_00415ed0 = DAT_00415ec8;
        DAT_00415ed4 = DAT_00415ecc;
        return 0;
      }
      if ((param_2 == 0x201) || (param_2 == 0x204)) {
        if ((*(byte *)(DAT_00415e84 + 4) & 2) == 0) {
          return 0;
        }
        DAT_00415ed0 = param_4 & 0xffff;
        DAT_00415ed4 = (uint)param_4._2_2_;
        DAT_00415ed8._0_1_ = 0x81;
        return 0;
      }
      if (param_2 == 0x202) {
        if ((*(byte *)(DAT_00415e84 + 4) & 2) == 0) {
          return 0;
        }
        DAT_00415ed0 = param_4 & 0xffff;
        DAT_00415ed4 = (uint)param_4._2_2_;
        DAT_00415ed8._0_1_ = 0x82;
        DAT_00415ed8._2_1_ = *(undefined1 *)(DAT_00415e84 + 0x30);
        return 0;
      }
      if (param_2 == 0x205) {
        if ((*(byte *)(DAT_00415e84 + 4) & 2) == 0) {
          return 0;
        }
        DAT_00415ed0 = param_4 & 0xffff;
        DAT_00415ed4 = (uint)param_4._2_2_;
        DAT_00415ed8._0_1_ = 0x83;
        DAT_00415ed8._2_1_ = *(undefined1 *)(DAT_00415e84 + 0x31);
        return 0;
      }
      if (param_2 == 0x20) {
        if ((short)param_4 == 1) {
          hCursor = FUN_0040a4b4();
          SetCursor(hCursor);
          return 0;
        }
      }
      else {
        if (param_2 == 0x200) {
          if ((*(byte *)(DAT_00415e84 + 4) & 2) == 0) {
            return 0;
          }
          DAT_00415ec8 = param_4 & 0xffff;
          DAT_00415ecc = (uint)param_4._2_2_;
          FUN_00404430(param_4._2_2_,(short)param_4);
          return 0;
        }
        if (param_2 == 0x24) {
          iVar5 = DAT_00415eb0 + DAT_00415ec0;
          *(int *)(param_4 + 0x20) = iVar5;
          *(int *)(param_4 + 8) = iVar5;
          iVar5 = DAT_00415eb4 + DAT_00415ec4;
          *(int *)(param_4 + 0x24) = iVar5;
          *(int *)(param_4 + 0xc) = iVar5;
          return 0;
        }
        if (param_2 == 5) {
          DAT_00415ea8 = param_4 & 0xffff;
          DAT_00415eac = (uint)param_4._2_2_;
          return 0;
        }
        if (param_2 == 0x3bd) {
          FUN_0040a290();
          return 0;
        }
        if (param_2 == 0x10) {
          FUN_0040a40c();
          return 0;
        }
        if (param_2 == 2) {
          PostQuitMessage(0);
          return 0;
        }
      }
      goto LAB_0040b066;
    }
    if (DAT_00415e8c != (HPALETTE)0x0) {
      SelectPalette(DAT_00415e88,DAT_00415e8c,0);
      UVar4 = RealizePalette(DAT_00415e88);
      if (UVar4 != 0) {
        FUN_0040426c(DAT_00415eac,DAT_00415ea8,0,0);
        return 1;
      }
    }
  }
  return 0;
}



/* Address: 0040b3e0 */

undefined4 FUN_0040b3e0(void)

{
  LRESULT LVar1;
  
  if ((DAT_00415ea8 <= DAT_00415eb8) && (DAT_00415eac <= DAT_00415ebc)) {
    FUN_0040ad04();
    LVar1 = FUN_0040ad7c(*(undefined4 *)(DAT_00415e7c + 0x34),*(undefined4 *)(DAT_00415e7c + 0x30),
                         *(undefined4 *)(DAT_00415e7c + 0x2c),*(undefined4 *)(DAT_00415e7c + 0x28),
                         (char *)0x0);
    if ((char)LVar1 == '\0') {
      ShowWindow(DAT_0041636c,DAT_00415e98);
      UpdateWindow(DAT_0041636c);
      LVar1 = SendMessageA(DAT_0041636c,0x30f,0,0);
    }
    return CONCAT31((int3)((uint)LVar1 >> 8),1);
  }
  FUN_0040bca8(0x4108bb);
  return 0;
}



/* Address: 0040b460 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_0040b460(int param_1)

{
  int iVar1;
  int iVar2;
  DWORD dwStyle;
  int X;
  int Y;
  WNDCLASSA WStack_28;
  
  X = -0x80000000;
  Y = -0x80000000;
  _DAT_00410648 = *(int *)(param_1 + 0xc);
  _DAT_0041064c = *(int *)(param_1 + 0x10);
  DAT_00415ea8 = _DAT_00410648;
  DAT_00415eac = _DAT_0041064c;
  DAT_00415eb0 = _DAT_00410648;
  DAT_00415eb4 = _DAT_0041064c;
  DAT_00415eb8 = GetSystemMetrics(0);
  DAT_00415ebc = GetSystemMetrics(1);
  DAT_00415ec0 = 0;
  iVar1 = GetSystemMetrics(4);
  iVar2 = GetSystemMetrics(6);
  DAT_00415ec4 = iVar1 - iVar2;
  if (*(char *)(param_1 + 0x24) == '\0') {
    iVar1 = GetSystemMetrics(5);
    DAT_00415ec0 = iVar1 * 2;
    iVar2 = GetSystemMetrics(6);
    DAT_00415ec4 = DAT_00415ec4 + iVar2 * 2;
    if ((DAT_00415eb8 == DAT_00415ea8) && (DAT_00415ebc == DAT_00415eac)) {
      X = -(DAT_00415ec0 - iVar1);
      Y = -(DAT_00415ec4 - iVar2);
    }
    dwStyle = 0x20a0000;
  }
  else {
    iVar1 = GetSystemMetrics(0x20);
    DAT_00415ec0 = iVar1 * 2;
    iVar2 = GetSystemMetrics(0x21);
    DAT_00415ec4 = DAT_00415ec4 + iVar2 * 2;
    if (DAT_00415eb8 <= DAT_00415ea8) {
      DAT_00415ea8 = DAT_00415eb8;
      X = -(DAT_00415ec0 - iVar1);
    }
    if (DAT_00415ebc <= DAT_00415eac) {
      DAT_00415eac = DAT_00415ebc;
      Y = -(DAT_00415ec4 - iVar2);
    }
    dwStyle = 0x2cf0000;
  }
  WStack_28.style = 0x1020;
  WStack_28.lpfnWndProc = FUN_0040af64;
  WStack_28.cbClsExtra = 0;
  WStack_28.cbWndExtra = 0;
  WStack_28.hInstance = DAT_00416368;
  WStack_28.hIcon = LoadIconA(DAT_00416368,(LPCSTR)0x64);
  WStack_28.hCursor = FUN_0040a4b4();
  WStack_28.hbrBackground = (HBRUSH)GetStockObject(4);
  WStack_28.lpszMenuName = (LPCSTR)0x0;
  WStack_28.lpszClassName = s_GMW_PLAYER_004108d8;
  RegisterClassA(&WStack_28);
  DAT_0041636c = CreateWindowExA(0,s_GMW_PLAYER_004108d8,(LPCSTR)(param_1 + 0x78),dwStyle,X,Y,
                                 DAT_00415ea8 + DAT_00415ec0,DAT_00415eac + DAT_00415ec4,(HWND)0x0,
                                 (HMENU)0x0,DAT_00416368,(LPVOID)0x0);
  DAT_00415e88 = GetDC(DAT_0041636c);
  return;
}



/* Address: 0040b664 */

bool FUN_0040b664(undefined4 param_1,int param_2,SIZE_T *param_3)

{
  LPVOID pvVar1;
  SIZE_T SVar2;
  HMODULE hModule;
  FARPROC pFVar3;
  
  DAT_00415e80 = param_3;
  DAT_00415e7c = param_2;
  DAT_00415e84 = param_1;
  if (DAT_0041636c == 0) {
    FUN_0040b460(param_2);
  }
  if (DAT_00415fe8 == 0) {
    hModule = GetModuleHandleA(s_GDI32_DLL_004108e3);
    if (hModule != (HMODULE)0x0) {
      pFVar3 = GetProcAddress(hModule,s_CreateDIBSection_004108ed);
      if (pFVar3 != (FARPROC)0x0) {
        DAT_00415fe4 = (HGDIOBJ)(*pFVar3)(DAT_00415e88,&DAT_00410644,1,&DAT_00415fe8,0,0);
        if (DAT_00415fe8 != 0) {
          DAT_00415fec = CreateCompatibleDC(DAT_00415e88);
          SelectObject(DAT_00415fec,DAT_00415fe4);
        }
      }
    }
  }
  SVar2 = DAT_00415fe8;
  param_3[3] = DAT_00415fe8;
  if (SVar2 != 0) {
    *param_3 = *param_3 - param_3[2];
  }
  *param_3 = *param_3 - 80000;
  if (DAT_00415e94 == (LPVOID)0x0) {
    DAT_00415e90 = GlobalAlloc(2,*param_3);
    DAT_00415e94 = GlobalLock(DAT_00415e90);
  }
  pvVar1 = DAT_00415e94;
  param_3[1] = (SIZE_T)DAT_00415e94;
  return pvVar1 != (LPVOID)0x0;
}



/* Address: 0040b754 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 FUN_0040b754(byte param_1)

{
  undefined4 in_EAX;
  
  _DAT_00415ea0 = 0;
  if (param_1 != 0) {
    in_EAX = (undefined4)(1000 / (ulonglong)(longlong)(int)(uint)param_1);
    _DAT_00415ea0 = in_EAX;
  }
  return CONCAT31((int3)((uint)in_EAX >> 8),1);
}



/* Address: 0040b780 */

bool FUN_0040b780(char param_1,char param_2,UINT param_3,BYTE *param_4)

{
  BYTE *pBVar1;
  BYTE BVar2;
  HBRUSH hbr;
  int iVar3;
  PALETTEENTRY *pPVar4;
  int iVar5;
  LOGPALETTE local_41c [123];
  tagPALETTEENTRY atStack_40 [10];
  HGDIOBJ pvStack_18;
  HPEN pHStack_14;
  RECT local_10;
  
  pPVar4 = local_41c[0].palPalEntry;
  if ((DAT_00415ea8 != 0) && (DAT_00415eac != 0)) {
    if (param_2 == '\0') {
      local_10.left = 0;
      local_10.top = 0;
      local_10.right = DAT_00415ea8;
      local_10.bottom = DAT_00415eac;
      hbr = (HBRUSH)GetStockObject(4);
      FillRect(DAT_00415e88,&local_10,hbr);
    }
    else {
      pHStack_14 = CreatePen(0,1,*(COLORREF *)(s_CreateDIBSection_004108ed + param_2 * 4 + 0xf));
      pvStack_18 = SelectObject(DAT_00415e88,pHStack_14);
      iVar5 = 0;
      do {
        iVar3 = iVar5;
        if (iVar5 < DAT_00415ea8) {
          do {
            MoveToEx(DAT_00415e88,iVar3,0,(LPPOINT)0x0);
            LineTo(DAT_00415e88,iVar3,DAT_00415eac);
            iVar3 = iVar3 + 8;
          } while (iVar3 < DAT_00415ea8);
        }
        iVar3 = iVar5;
        if (iVar5 < DAT_00415eac) {
          do {
            MoveToEx(DAT_00415e88,0,iVar3,(LPPOINT)0x0);
            LineTo(DAT_00415e88,DAT_00415ea8,iVar3);
            iVar3 = iVar3 + 8;
          } while (iVar3 < DAT_00415eac);
        }
        iVar5 = iVar5 + 1;
      } while (iVar5 < 8);
      SelectObject(DAT_00415e88,pvStack_18);
      DeleteObject(pHStack_14);
    }
  }
  local_41c[0].palVersion = 0x300;
  local_41c[0].palNumEntries = (WORD)param_3;
  if (0 < (int)param_3) {
    iVar5 = 0;
    do {
      pPVar4->peRed = *param_4;
      pBVar1 = param_4 + 2;
      pPVar4->peGreen = param_4[1];
      param_4 = param_4 + 3;
      pPVar4->peBlue = *pBVar1;
      pPVar4->peFlags = '\x04';
      pPVar4 = pPVar4 + 1;
      iVar5 = iVar5 + 1;
    } while (iVar5 < (int)param_3);
  }
  if (param_1 != '\0') {
    GetSystemPaletteEntries(DAT_00415e88,0,10,local_41c[0].palPalEntry);
    GetSystemPaletteEntries(DAT_00415e88,0xf6,10,atStack_40);
  }
  if (DAT_00415e8c != (HPALETTE)0x0) {
    DeleteObject(DAT_00415e8c);
  }
  DAT_00415e8c = CreatePalette(local_41c);
  if (DAT_00415fec != (HDC)0x0) {
    pPVar4 = local_41c[0].palPalEntry;
    iVar5 = 0;
    if (0 < (int)param_3) {
      do {
        BVar2 = pPVar4->peRed;
        pPVar4->peRed = pPVar4->peBlue;
        pPVar4->peBlue = BVar2;
        pPVar4 = pPVar4 + 1;
        iVar5 = iVar5 + 1;
      } while (iVar5 < (int)param_3);
    }
    SetDIBColorTable(DAT_00415fec,0,param_3,(RGBQUAD *)local_41c[0].palPalEntry);
  }
  if (DAT_0041636c != (HWND)0x0) {
    SendMessageA(DAT_0041636c,0x30f,0,0);
  }
  return (bool)('\x01' - (DAT_00415e8c == (HPALETTE)0x0));
}



/* Address: 0040ba1c */

void FUN_0040ba1c(int param_1,int param_2,int param_3,int param_4,int param_5,void *param_6)

{
  if (DAT_00415fec == (HDC)0x0) {
    StretchDIBits(DAT_00415e88,param_5,param_4,param_3,param_2,param_5,(param_1 - param_4) - param_2
                  ,param_3,param_2,param_6,(BITMAPINFO *)&DAT_00410644,1,0xcc0020);
  }
  else {
    BitBlt(DAT_00415e88,param_5,param_4,param_3,param_2,DAT_00415fec,param_5,param_4,0xcc0020);
  }
  return;
}



/* Address: 0040ba8c */

undefined4
FUN_0040ba8c(char param_1,int param_2,undefined4 param_3,int param_4,int param_5,int param_6,
            int param_7,void *param_8)

{
  undefined4 in_EAX;
  int iVar1;
  int iVar2;
  int local_c;
  int *local_8;
  int *local_4;
  
  if ((DAT_00415ea8 != 0) && (DAT_00415eac != 0)) {
    if (param_1 == '\0') {
      in_EAX = FUN_0040ba1c(param_2,param_4,param_5,param_6,param_7,param_8);
    }
    else {
      local_c = 0;
      local_8 = &DAT_00410950;
      local_4 = &DAT_00410910;
      do {
        iVar2 = *local_4;
        if (iVar2 < DAT_00415ea8) {
          do {
            iVar1 = *local_8;
            if (iVar1 < DAT_00415eac) {
              do {
                FUN_0040ba1c(param_2,0x10,0x10,iVar1,iVar2,param_8);
                GdiFlush();
                iVar1 = iVar1 + 0x40;
              } while (iVar1 < DAT_00415eac);
            }
            iVar2 = iVar2 + 0x40;
          } while (iVar2 < DAT_00415ea8);
        }
        in_EAX = 0;
        local_8 = local_8 + 1;
        local_4 = local_4 + 1;
        local_c = local_c + 1;
      } while (local_c < 0x10);
    }
  }
  return CONCAT31((int3)((uint)in_EAX >> 8),1);
}



/* Address: 0040bb58 */

void FUN_0040bb58(void)

{
  GdiFlush();
  return;
}



/* Address: 0040bb60 */

undefined1 FUN_0040bb60(DWORD *param_1)

{
  undefined1 uVar1;
  int iVar2;
  DWORD DVar3;
  tagMSG local_1c;
  
  if (*param_1 == 0) {
    DAT_00415ed8._0_1_ = 0xe;
  }
  iVar2 = PeekMessageA(&local_1c,(HWND)0x0,0,0,1);
  while( true ) {
    if (iVar2 == 0) {
      DVar3 = timeGetTime();
      *param_1 = DVar3;
      uVar1 = (undefined1)DAT_00415ed8;
      DAT_00415ed8._0_1_ = 0xe;
      return uVar1;
    }
    if (local_1c.message == 0x12) break;
    TranslateMessage(&local_1c);
    DispatchMessageA(&local_1c);
    iVar2 = PeekMessageA(&local_1c,(HWND)0x0,0,0,1);
  }
  DAT_00415e9c = 1;
  return 0x83;
}



/* Address: 0040bbec */

void FUN_0040bbec(short *param_1)

{
  UINT uCommand;
  
  uCommand = 0x105;
  if (*param_1 == 0x3f) {
    param_1 = (short *)0x0;
    uCommand = 3;
  }
  WinHelpA(DAT_0041636c,&DAT_00415ee4,uCommand,(ULONG_PTR)param_1);
  return;
}



/* Address: 0040bc1c */

undefined4 FUN_0040bc1c(LPSTR param_1)

{
  BOOL BVar1;
  DWORD DVar2;
  undefined4 uVar3;
  LPBYTE pBVar4;
  DWORD *pDVar5;
  _STARTUPINFOA *p_Var6;
  _STARTUPINFOA local_54;
  _PROCESS_INFORMATION local_10;
  
  pDVar5 = &DAT_00410990;
  p_Var6 = &local_54;
  for (pBVar4 = (LPBYTE)0x11; pBVar4 != (LPBYTE)0x0; pBVar4 = pBVar4 + -1) {
    p_Var6->cb = *pDVar5;
    pDVar5 = pDVar5 + 1;
    p_Var6 = (_STARTUPINFOA *)&p_Var6->lpReserved;
  }
  local_10.hProcess = DAT_004109d4;
  local_10.hThread = DAT_004109d8;
  local_10.dwProcessId = DAT_004109dc;
  local_10.dwThreadId = DAT_004109e0;
  local_54.cb = 0x44;
  local_54.cbReserved2 = 0;
  local_54.lpReserved = (LPSTR)pBVar4;
  local_54.lpDesktop = (LPSTR)pBVar4;
  local_54.dwFlags = (DWORD)pBVar4;
  local_54.lpReserved2 = pBVar4;
  BVar1 = CreateProcessA((LPCSTR)0x0,param_1,(LPSECURITY_ATTRIBUTES)0x0,(LPSECURITY_ATTRIBUTES)0x0,1
                         ,0x20,(LPVOID)0x0,(LPCSTR)0x0,&local_54,&local_10);
  if (BVar1 == 0) {
    uVar3 = 0;
  }
  else {
    DVar2 = WaitForSingleObject(local_10.hProcess,0xffffffff);
    uVar3 = 1;
    if (DVar2 == 0xffffffff) {
      uVar3 = 0;
    }
  }
  return uVar3;
}



/* Address: 0040bca8 */

undefined4 FUN_0040bca8(int param_1)

{
  if (DAT_00415e9c == 0) {
    DAT_00415e9c = param_1;
  }
  return 0;
}



/* Address: 0040bcc0 */

uint FUN_0040bcc0(LPCSTR param_1)

{
  uint unaff_retaddr;
  
  DAT_00415edc = FUN_0040d564(param_1,0x8000,unaff_retaddr);
  return -(uint)(DAT_00415edc != 0) - DAT_00415edc >> 0x1f;
}



/* Address: 0040bce4 */

void FUN_0040bce4(void)

{
  FUN_0040d7d4(DAT_00415edc,0,1);
  return;
}



/* Address: 0040bcf8 */

bool FUN_0040bcf8(DWORD param_1,LONG param_2)

{
  DWORD DVar1;
  
  DVar1 = FUN_0040d7d4(DAT_00415edc,param_2,param_1);
  return DVar1 != 0xffffffff;
}



/* Address: 0040bd20 */

bool __fastcall FUN_0040bd20(DWORD param_1,char *param_2,DWORD param_3,char *param_4)

{
  DWORD DVar1;
  
  DVar1 = FUN_0040da70(param_1,param_2,DAT_00415edc,param_4,param_3);
  return DVar1 == param_3;
}



/* Address: 0040bd48 */

void __fastcall FUN_0040bd48(DWORD param_1,char *param_2,DWORD param_3,char *param_4)

{
  FUN_0040da70(param_1,param_2,DAT_00415edc,param_4,param_3);
  return;
}



/* Address: 0040bd64 */

void __fastcall FUN_0040bd64(undefined4 param_1,undefined4 param_2)

{
  if (0 < DAT_00415edc) {
    FUN_0040d764(param_1,param_2,DAT_00415edc);
    DAT_00415edc = -1;
  }
  return;
}



/* Address: 0040bd84 */

void FUN_0040bd84(undefined4 param_1,undefined4 param_2,uint *param_3,LPSTR param_4)

{
  uint *puVar1;
  uint *puVar2;
  uint local_80 [32];
  
  puVar2 = (uint *)&DAT_004109ec;
  puVar1 = FUN_0040c5d8(local_80,param_3);
  puVar1 = (uint *)FUN_0040d350((char *)puVar1);
  puVar1 = FUN_0040d378(puVar1,puVar2);
  *(undefined1 *)puVar1 = 0;
  wsprintfA(param_4,s__s_d__s_004109e4,local_80,param_1,param_2);
  return;
}



/* Address: 0040bde8 */

void FUN_0040bde8(uint *param_1,LPSTR param_2)

{
  char *pcVar1;
  CHAR local_80 [128];
  
  GetTempPathA(0x80,local_80);
  pcVar1 = FUN_0040d2fc(param_1,'\\');
  wsprintfA(param_2,s__s__s_004109f1,local_80,pcVar1);
  return;
}



/* Address: 0040be34 */

uint FUN_0040be34(undefined4 param_1,undefined4 param_2,uint *param_3)

{
  uint local_100 [32];
  CHAR local_80 [128];
  
  FUN_0040bd84(param_1,param_2,param_3,(LPSTR)local_100);
  DAT_00415ee0 = FUN_0040d564((LPCSTR)local_100,0x8102,0x180);
  if (DAT_00415ee0 < 1) {
    FUN_0040bde8(local_100,local_80);
    DAT_00415ee0 = FUN_0040d564(local_80,0x8102,0x180);
  }
  return -(uint)(DAT_00415ee0 != 0) - DAT_00415ee0 >> 0x1f;
}



/* Address: 0040bec0 */

undefined4 FUN_0040bec0(undefined4 param_1,undefined4 param_2,uint *param_3)

{
  int iVar1;
  uint local_100 [32];
  CHAR local_80 [128];
  
  FUN_0040bd84(param_1,param_2,param_3,(LPSTR)local_100);
  iVar1 = FUN_0040d7b8((LPCSTR)local_100);
  if (iVar1 != 0) {
    FUN_0040bde8(local_100,local_80);
    iVar1 = FUN_0040d7b8(local_80);
    if (iVar1 != 0) {
      return 0;
    }
  }
  return CONCAT31((int3)((uint)iVar1 >> 8),1);
}



/* Address: 0040bf30 */

bool __fastcall FUN_0040bf30(DWORD param_1,char *param_2,DWORD param_3,char *param_4)

{
  DWORD DVar1;
  
  DVar1 = FUN_0040da70(param_1,param_2,DAT_00415ee0,param_4,param_3);
  return DVar1 == param_3;
}



/* Address: 0040bf58 */

bool __fastcall FUN_0040bf58(char *param_1,char *param_2,char *param_3,char *param_4)

{
  char *pcVar1;
  
  pcVar1 = FUN_0040d840(param_1,param_2,DAT_00415ee0,param_4,param_3);
  return pcVar1 == param_3;
}



/* Address: 0040bf80 */

void FUN_0040bf80(void)

{
  FUN_0040d7d4(DAT_00415ee0,0,1);
  return;
}



/* Address: 0040bf94 */

bool FUN_0040bf94(LONG param_1)

{
  DWORD DVar1;
  
  DVar1 = FUN_0040d7d4(DAT_00415ee0,param_1,0);
  return DVar1 != 0xffffffff;
}



/* Address: 0040bfb8 */

void FUN_0040bfb8(void)

{
  FUN_0040d800(DAT_00415ee0,0);
  return;
}



/* Address: 0040bfcc */

void __fastcall FUN_0040bfcc(undefined4 param_1,undefined4 param_2)

{
  if (0 < DAT_00415ee0) {
    FUN_0040d764(param_1,param_2,DAT_00415ee0);
    DAT_00415ee0 = -1;
  }
  return;
}



/* Address: 0040bfec */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

WPARAM FUN_0040bfec(HMODULE param_1,undefined4 param_2,undefined4 param_3,undefined4 param_4)

{
  uint *puVar1;
  undefined4 uVar2;
  BOOL BVar3;
  DWORD DVar4;
  uint local_128 [64];
  tagMSG tStack_28;
  uint uStack_c;
  tagPOINT tStack_8;
  
  GetModuleFileNameA(param_1,(LPSTR)local_128,0x100);
  DAT_00416368 = param_1;
  DAT_00415e98 = param_4;
  DAT_00415e88 = 0;
  DAT_00415e8c = 0;
  DAT_00415e90 = 0;
  DAT_00415e94 = 0;
  DAT_00415e9c = 0;
  _DAT_00415ea0 = 0;
  DAT_00415fe4 = 0;
  DAT_00415fe8 = 0;
  DAT_00415fec = 0;
  DAT_00416044 = 0;
  DAT_0041636c = (HWND)0x0;
  DAT_00415ed4 = 0;
  DAT_00415ed0 = 0;
  DAT_00415ecc = 0;
  DAT_00415ec8 = 0;
  DAT_00415eac = 0;
  DAT_00415ea8 = 0;
  DAT_00415ee0 = 0xffffffff;
  DAT_00415edc._0_1_ = 0xff;
  DAT_00415edc._1_1_ = 0xff;
  DAT_00415edc._2_2_ = 0xffff;
  DAT_00415ed8._2_1_ = 0xe;
  DAT_00415ed8._1_1_ = 0xe;
  DAT_00415ed8._0_1_ = '\x0e';
  puVar1 = FUN_0040c5d8((uint *)&DAT_00415e3c,local_128);
  uVar2 = FUN_004040e4(puVar1);
  if ((char)uVar2 == '\0') {
    FUN_0040a3b8();
    FUN_0040a40c();
    FUN_0040a438();
    return 0;
  }
  FUN_0040a3d8(local_128);
  while( true ) {
    do {
      do {
        while( true ) {
          while( true ) {
            BVar3 = PeekMessageA(&tStack_28,(HWND)0x0,0,0,1);
            if (BVar3 == 0) break;
            if (tStack_28.message == 0x12) goto LAB_0040c207;
            TranslateMessage(&tStack_28);
            DispatchMessageA(&tStack_28);
          }
          if (DAT_00415e9c == 0) break;
          if (DAT_00415e9c == 1) goto LAB_0040c207;
          FUN_0040a3b8();
          FUN_0040a40c();
        }
        BVar3 = IsIconic(DAT_0041636c);
      } while (BVar3 != 0);
      DVar4 = timeGetTime();
    } while (DVar4 <= DAT_00416050 + _DAT_00415ea0);
    uStack_c = CONCAT31(uStack_c._1_3_,1);
    DAT_00416050 = DVar4;
    if ((*(byte *)(DAT_00415e84 + 4) & 2) != 0) {
      GetCursorPos(&tStack_8);
      ScreenToClient(DAT_0041636c,&tStack_8);
      if ((((tStack_8.x < 0) || (DAT_00415ea8 <= tStack_8.x)) || (tStack_8.y < 0)) ||
         (DAT_00415eac <= tStack_8.y)) {
        uStack_c = uStack_c & 0xffffff00;
      }
    }
    uVar2 = FUN_00404170((char)uStack_c,DAT_00415ed8._2_1_,DAT_00415ed8._1_1_,(char)DAT_00415ed8,
                         DAT_00415ed4,DAT_00415ed0,DAT_00415ecc,DAT_00415ec8,DAT_00415eac,
                         DAT_00415ea8);
    if ((char)uVar2 == '\0') break;
    DAT_00415ed8._2_1_ = 0xe;
    DAT_00415ed8._1_1_ = 0xe;
    DAT_00415ed8._0_1_ = '\x0e';
  }
  FUN_0040a40c();
LAB_0040c207:
  FUN_0040a438();
  return tStack_28.wParam;
}



/* Address: 0040c2a0 */

undefined4 * __cdecl FUN_0040c2a0(undefined4 *param_1,undefined4 *param_2,uint param_3)

{
  undefined1 *puVar1;
  undefined1 uVar2;
  undefined4 uVar3;
  undefined4 *puVar4;
  uint uVar5;
  undefined4 *puVar6;
  undefined4 *puVar7;
  
  if ((param_3 == 0) || (param_2 == param_1)) {
    return param_1;
  }
  if ((param_2 <= param_1) && (param_1 < (undefined4 *)(param_3 + (int)param_2))) {
    puVar6 = (undefined4 *)((int)param_2 + (param_3 - 4));
    puVar7 = (undefined4 *)((int)param_1 + (param_3 - 4));
    if ((3 < param_3) && (puVar4 = puVar7, ((uint)puVar6 & 3) != 0)) {
      while (((uint)puVar4 & 3) != 0) {
        *(undefined1 *)((int)puVar7 + 3) = *(undefined1 *)((int)puVar6 + 3);
        puVar7 = (undefined4 *)((int)puVar7 + -1);
        param_3 = param_3 - 1;
        puVar6 = (undefined4 *)((int)puVar6 - 1);
        puVar4 = puVar6;
      }
    }
    uVar5 = param_3 >> 2;
    switch(uVar5) {
    case 0:
      break;
    case 5:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 4:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 3:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 2:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 1:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
      break;
    default:
      for (; uVar5 != 0; uVar5 = uVar5 - 1) {
        *puVar7 = *puVar6;
        puVar6 = puVar6 + -1;
        puVar7 = puVar7 + -1;
      }
      switch(param_3 & 3) {
      case 0:
        goto switchD_0040c422_caseD_0;
      case 1:
        goto switchD_0040c422_caseD_1;
      case 3:
        goto switchD_0040c422_caseD_3;
      }
      goto switchD_0040c422_caseD_2;
    }
    switch(param_3 & 3) {
    case 0:
switchD_0040c422_caseD_0:
      return param_1;
    case 3:
switchD_0040c422_caseD_3:
      puVar1 = (undefined1 *)((int)puVar6 + 3);
      puVar6 = (undefined4 *)((int)puVar6 - 1);
      *(undefined1 *)((int)puVar7 + 3) = *puVar1;
      puVar7 = (undefined4 *)((int)puVar7 + -1);
    case 2:
switchD_0040c422_caseD_2:
      puVar1 = (undefined1 *)((int)puVar6 + 3);
      puVar6 = (undefined4 *)((int)puVar6 - 1);
      *(undefined1 *)((int)puVar7 + 3) = *puVar1;
      puVar7 = (undefined4 *)((int)puVar7 + -1);
    case 1:
switchD_0040c422_caseD_1:
      *(undefined1 *)((int)puVar7 + 3) = *(undefined1 *)((int)puVar6 + 3);
      return param_1;
    }
  }
  puVar6 = param_1;
  if ((3 < param_3) && (((uint)param_2 & 3) != 0)) {
    uVar5 = (uint)param_1 & 3;
    while (uVar5 != 0) {
      uVar2 = *(undefined1 *)param_2;
      param_2 = (undefined4 *)((int)param_2 + 1);
      *(undefined1 *)puVar6 = uVar2;
      puVar6 = (undefined4 *)((int)puVar6 + 1);
      param_3 = param_3 - 1;
      uVar5 = (uint)param_2 & 3;
    }
  }
  uVar5 = param_3 >> 2;
  switch(uVar5) {
  case 0:
    break;
  case 5:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 4:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 3:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 2:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 1:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
    break;
  default:
    for (; uVar5 != 0; uVar5 = uVar5 - 1) {
      *puVar6 = *param_2;
      param_2 = param_2 + 1;
      puVar6 = puVar6 + 1;
    }
    switch(param_3 & 3) {
    case 0:
      goto switchD_0040c36e_caseD_0;
    case 1:
      goto switchD_0040c36e_caseD_1;
    case 3:
      goto switchD_0040c36e_caseD_3;
    }
    goto switchD_0040c36e_caseD_2;
  }
  switch(param_3 & 3) {
  case 0:
switchD_0040c36e_caseD_0:
    return param_1;
  case 3:
switchD_0040c36e_caseD_3:
    uVar2 = *(undefined1 *)param_2;
    param_2 = (undefined4 *)((int)param_2 + 1);
    *(undefined1 *)puVar6 = uVar2;
    puVar6 = (undefined4 *)((int)puVar6 + 1);
  case 2:
switchD_0040c36e_caseD_2:
    uVar2 = *(undefined1 *)param_2;
    param_2 = (undefined4 *)((int)param_2 + 1);
    *(undefined1 *)puVar6 = uVar2;
    puVar6 = (undefined4 *)((int)puVar6 + 1);
  case 1:
switchD_0040c36e_caseD_1:
    *(undefined1 *)puVar6 = *(undefined1 *)param_2;
    return param_1;
  }
}



/* Address: 0040c42c */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

int __cdecl FUN_0040c42c(int *param_1)

{
  int iVar1;
  _SYSTEMTIME local_34;
  uint uStack_24;
  uint uStack_20;
  uint uStack_1c;
  uint uStack_18;
  uint uStack_14;
  uint uStack_10;
  
  GetLocalTime(&local_34);
  uStack_24 = (uint)local_34.wSecond;
  uStack_20 = (uint)local_34.wMinute;
  uStack_1c = (uint)local_34.wHour;
  uStack_10 = (uint)local_34.wYear;
  uStack_14 = (uint)local_34.wMonth;
  uStack_18 = (uint)local_34.wDay;
  _DAT_0041675c = local_34.wMilliseconds;
  iVar1 = FUN_0040dc9c((int *)&uStack_24);
  if (param_1 != (int *)0x0) {
    *param_1 = iVar1;
  }
  return iVar1;
}



/* Address: 0040c4a0 */

int __cdecl FUN_0040c4a0(int param_1,int param_2)

{
  int iVar1;
  int extraout_ECX;
  int extraout_ECX_00;
  int *unaff_FS_OFFSET;
  
  iVar1 = *(int *)(param_1 + 0x18);
  if (iVar1 != *unaff_FS_OFFSET) {
    FUN_0040df44(iVar1,(PEXCEPTION_RECORD)0x0);
    param_1 = extraout_ECX;
  }
  if (iVar1 != 0) {
    if (*(int *)(param_1 + 0x20) == -0x7c987f80) {
      if (*(code **)(param_1 + 0x24) != (code *)0x0) {
        (**(code **)(param_1 + 0x24))();
      }
    }
    else {
      FUN_0040df6c(iVar1,-1);
      param_1 = extraout_ECX_00;
    }
  }
  if (param_2 == 0) {
    param_2 = 1;
  }
  **(undefined4 **)(param_1 + 0x10) = *(undefined4 *)(param_1 + 0x14);
  return param_2;
}



/* Address: 0040c500 */

undefined4 __cdecl FUN_0040c500(undefined4 *param_1,int param_2,int param_3,undefined4 param_4)

{
  int iVar1;
  uint uVar2;
  undefined4 unaff_EBX;
  undefined4 unaff_EBP;
  undefined4 unaff_ESI;
  undefined4 *puVar3;
  undefined4 unaff_EDI;
  undefined4 *puVar4;
  int *unaff_FS_OFFSET;
  undefined4 unaff_retaddr;
  
  param_1[4] = register0x00000010;
  *param_1 = unaff_EBP;
  param_1[1] = unaff_EBX;
  param_1[2] = unaff_EDI;
  param_1[3] = unaff_ESI;
  param_1[8] = 0x83678080;
  param_1[5] = unaff_retaddr;
  *(undefined1 *)(param_1 + 9) = 0;
  iVar1 = *unaff_FS_OFFSET;
  param_1[6] = iVar1;
  if (iVar1 != -1) {
    if ((param_2 == 0) || (param_1[9] = param_3, iVar1 = param_3, param_2 == 1)) {
      param_1[7] = *(undefined4 *)(iVar1 + 0xc);
    }
    else {
      param_1[7] = param_4;
      uVar2 = param_2 - 2;
      if (uVar2 != 0) {
        if (6 < uVar2) {
          uVar2 = 6;
        }
        puVar3 = (undefined4 *)&stack0x00000014;
        puVar4 = param_1 + 10;
        for (; uVar2 != 0; uVar2 = uVar2 - 1) {
          *puVar4 = *puVar3;
          puVar3 = puVar3 + 1;
          puVar4 = puVar4 + 1;
        }
      }
    }
  }
  return 0;
}



/* Address: 0040c58c */

int __cdecl FUN_0040c58c(byte *param_1)

{
  byte bVar1;
  uint uVar2;
  uint uVar3;
  byte *pbVar4;
  byte *pbVar5;
  
  do {
    do {
      pbVar5 = param_1 + 1;
      bVar1 = *param_1;
      param_1 = pbVar5;
    } while (bVar1 == 0x20);
  } while ((8 < bVar1) && (bVar1 < 0xe));
  uVar2 = 0;
  uVar3 = 0;
  pbVar4 = pbVar5;
  if (bVar1 != 0x2b) {
    if (bVar1 != 0x2d) goto LAB_0040c5b9;
    uVar3 = 0xffffffff;
  }
  while( true ) {
    pbVar5 = pbVar4 + 1;
    bVar1 = *pbVar4;
LAB_0040c5b9:
    if ((bVar1 < 0x30) || (9 < (byte)(bVar1 - 0x30))) break;
    uVar2 = uVar2 * 10 + (uint)(byte)(bVar1 - 0x30);
    pbVar4 = pbVar5;
  }
  return (uVar2 ^ uVar3) - uVar3;
}



/* Address: 0040c5d8 */

uint * __cdecl FUN_0040c5d8(uint *param_1,uint *param_2)

{
  uint uVar1;
  char cVar2;
  char cVar3;
  uint *puVar4;
  
  puVar4 = param_1;
  while (((uint)param_2 & 3) != 0) {
    uVar1 = *param_2;
    param_2 = (uint *)((int)param_2 + 1);
    *(char *)puVar4 = (char)uVar1;
    puVar4 = (uint *)((int)puVar4 + 1);
    if ((char)uVar1 == '\0') {
      return param_1;
    }
  }
  do {
    uVar1 = *param_2;
    param_2 = param_2 + 1;
    cVar2 = (char)uVar1;
    if (cVar2 == '\0') {
      *(char *)puVar4 = '\0';
      return param_1;
    }
    cVar3 = (char)(uVar1 >> 8);
    if (cVar3 == '\0') {
      *(char *)puVar4 = cVar2;
      *(char *)((int)puVar4 + 1) = '\0';
      return param_1;
    }
    if ((uVar1 & 0xff0000) == 0) {
      *(char *)puVar4 = cVar2;
      *(char *)((int)puVar4 + 1) = cVar3;
      *(char *)((int)puVar4 + 2) = '\0';
      return param_1;
    }
    *puVar4 = uVar1;
    puVar4 = puVar4 + 1;
  } while ((uVar1 & 0xff000000) != 0);
  return param_1;
}



/* Address: 0040c63c */

char * __cdecl FUN_0040c63c(uint *param_1)

{
  uint uVar1;
  uint *puVar2;
  uint *puVar3;
  
  puVar3 = param_1;
  while (puVar2 = puVar3, ((uint)puVar2 & 3) != 0) {
    puVar3 = (uint *)((int)puVar2 + 1);
    if ((char)*puVar2 == '\0') {
      return (char *)((int)puVar2 - (int)param_1);
    }
  }
  do {
    puVar3 = puVar2;
    uVar1 = *puVar3;
    if ((char)uVar1 == '\0') {
      return (char *)((int)puVar3 - (int)param_1);
    }
    if ((char)(uVar1 >> 8) == '\0') {
      return (char *)((int)puVar3 + (1 - (int)param_1));
    }
    if ((uVar1 & 0xff0000) == 0) {
      return (char *)((int)puVar3 + (2 - (int)param_1));
    }
    puVar2 = puVar3 + 1;
  } while ((uVar1 & 0xff000000) != 0);
  return (char *)((int)puVar3 + (3 - (int)param_1));
}



/* Address: 0040c69c */

char * __cdecl FUN_0040c69c(char *param_1,char param_2)

{
  undefined4 uVar1;
  char *pcVar2;
  char cVar3;
  
  while (pcVar2 = param_1, ((uint)param_1 & 3) != 0) {
    cVar3 = *param_1;
    param_1 = param_1 + 1;
    if (cVar3 == param_2) goto LAB_0040c6df;
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
  }
  while( true ) {
    uVar1 = *(undefined4 *)pcVar2;
    param_1 = pcVar2 + 4;
    if ((char)uVar1 == param_2) {
      return pcVar2;
    }
    if ((char)uVar1 == '\0') {
      return (char *)0x0;
    }
    cVar3 = (char)((uint)uVar1 >> 8);
    if (cVar3 == param_2) {
      return pcVar2 + 1;
    }
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
    cVar3 = (char)((uint)uVar1 >> 0x10);
    if (cVar3 == param_2) {
      return pcVar2 + 2;
    }
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
    cVar3 = (char)((uint)uVar1 >> 0x18);
    if (cVar3 == param_2) break;
    pcVar2 = param_1;
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
  }
LAB_0040c6df:
  return param_1 + -1;
}



/* Address: 0040c6f0 */

int __cdecl FUN_0040c6f0(int param_1)

{
  if ((0x40 < param_1) && (param_1 < 0x5b)) {
    param_1 = param_1 + 0x20;
  }
  return param_1;
}



/* Address: 0040c708 */

int __cdecl FUN_0040c708(int param_1)

{
  if ((0x60 < param_1) && (param_1 < 0x7b)) {
    param_1 = param_1 + -0x20;
  }
  return param_1;
}



/* Address: 0040c720 */

char * __cdecl FUN_0040c720(uint param_1,char *param_2,uint param_3)

{
  uint uVar1;
  undefined1 *puVar2;
  char cVar3;
  int iVar5;
  char *pcVar6;
  char *pcVar7;
  char local_7 [3];
  char cVar4;
  
  iVar5 = 1;
  pcVar6 = local_7 + 2;
  local_7[2] = 0;
  if ((param_3 == 10) && ((int)param_1 < 0)) {
    iVar5 = 0;
    param_1 = -param_1;
  }
  do {
    pcVar7 = pcVar6;
    pcVar6 = pcVar7 + -1;
    uVar1 = param_1 / param_3;
    cVar4 = (char)(param_1 % param_3);
    cVar3 = cVar4 + '0';
    if ('9' < cVar3) {
      cVar3 = cVar4 + '7';
    }
    *pcVar6 = cVar3;
    param_1 = uVar1;
  } while (uVar1 != 0);
  pcVar7 = pcVar7 + -2;
  *pcVar7 = '-';
  puVar2 = &stack0xfffffffc + -(int)(pcVar7 + iVar5);
  pcVar6 = pcVar7 + iVar5;
  pcVar7 = param_2;
  for (; puVar2 != (undefined1 *)0x0; puVar2 = puVar2 + -1) {
    *pcVar7 = *pcVar6;
    pcVar6 = pcVar6 + 1;
    pcVar7 = pcVar7 + 1;
  }
  return param_2;
}



/* Address: 0040c780 */

uint * __cdecl FUN_0040c780(uint *param_1,uint *param_2,uint param_3)

{
  uint uVar1;
  uint uVar2;
  uint *puVar3;
  
  uVar1 = 0xffffffff;
  puVar3 = param_2;
  do {
    if (uVar1 == 0) break;
    uVar1 = uVar1 - 1;
    uVar2 = *puVar3;
    puVar3 = (uint *)((int)puVar3 + 1);
  } while ((char)uVar2 != '\0');
  uVar2 = ~uVar1;
  if (param_3 <= ~uVar1) {
    uVar2 = param_3;
  }
  puVar3 = param_1;
  for (uVar1 = uVar2 >> 2; uVar1 != 0; uVar1 = uVar1 - 1) {
    *puVar3 = *param_2;
    param_2 = param_2 + 1;
    puVar3 = puVar3 + 1;
  }
  for (uVar1 = uVar2 & 3; uVar1 != 0; uVar1 = uVar1 - 1) {
    *(char *)puVar3 = (char)*param_2;
    param_2 = (uint *)((int)param_2 + 1);
    puVar3 = (uint *)((int)puVar3 + 1);
  }
  for (uVar1 = param_3 - uVar2 >> 2; uVar1 != 0; uVar1 = uVar1 - 1) {
    *puVar3 = uVar2 & 0xffffff00;
    puVar3 = puVar3 + 1;
  }
  for (uVar1 = param_3 - uVar2 & 3; uVar1 != 0; uVar1 = uVar1 - 1) {
    *(undefined1 *)puVar3 = 0;
    puVar3 = (uint *)((int)puVar3 + 1);
  }
  return param_1;
}



/* Address: 0040c810 */

void __cdecl FUN_0040c810(undefined4 param_1,byte *param_2)

{
  FUN_0040c834(&LAB_0040c7f0,&LAB_0040c7d0,&param_1,param_2,(undefined4 *)&stack0x0000000c);
  return;
}



/* Address: 0040c834 */

int __cdecl
FUN_0040c834(undefined *param_1,undefined *param_2,undefined4 param_3,byte *param_4,
            undefined4 *param_5)

{
  byte bVar1;
  longlong lVar2;
  uint uVar3;
  byte *pbVar4;
  int iVar5;
  int iVar6;
  bool bVar7;
  bool bVar8;
  longlong lVar9;
  int iStack_3c;
  int iStack_38;
  int local_34;
  int local_30;
  int iStack_2c;
  uint uStack_28;
  uint local_24;
  longlong *plStack_20;
  byte *local_1c;
  undefined4 local_18;
  code *local_14;
  byte local_10;
  int iStack_c;
  int iStack_8;
  int iStack_4;
  
  local_18 = param_3;
  local_1c = param_4;
  local_14 = (code *)param_2;
  local_30 = 0;
  local_34 = 0;
  local_24 = 0x20;
LAB_0040c865:
  do {
    while( true ) {
      local_10 = *local_1c;
      local_1c = local_1c + 1;
      if (local_10 == 0) {
        return local_30;
      }
      if (local_24 != 0xffffffff) {
        local_34 = local_34 + 1;
        local_24 = (*local_14)(local_18);
        while( true ) {
          if (local_24 == 0xffffffff) goto LAB_0040cba5;
          if (((local_10 != 0x20) && (local_10 != 9)) && (local_10 != 10)) break;
          do {
            do {
              local_10 = *local_1c;
              local_1c = local_1c + 1;
            } while (local_10 == 0x20);
          } while ((local_10 == 9) || (local_10 == 10));
          bVar1 = (&DAT_004109f9)[local_24];
          while ((bVar1 & 8) != 0) {
            local_34 = local_34 + 1;
            local_24 = (*local_14)(local_18);
            bVar1 = (&DAT_004109f9)[local_24];
          }
          if (local_10 == 0) goto LAB_0040cff5;
        }
      }
      if (local_10 == 0x25) break;
LAB_0040cc41:
      if (local_10 != local_24) {
LAB_0040cff5:
        (*(code *)param_1)(local_24,local_18);
        return local_30;
      }
    }
    uStack_28 = 0;
    local_10 = *local_1c;
    pbVar4 = local_1c + 1;
    if (local_10 == 0x2a) {
      uStack_28 = 2;
      local_10 = local_1c[1];
      pbVar4 = local_1c + 2;
    }
    local_1c = pbVar4;
    uVar3 = uStack_28;
    if (((&DAT_004109f9)[local_10] & 4) == 0) {
      iStack_2c = 0x7fff;
    }
    else {
      iStack_2c = 0;
      bVar1 = (&DAT_004109f9)[local_10];
      while ((bVar1 & 4) != 0) {
        iStack_2c = iStack_2c * 10 + (uint)local_10 + -0x30;
        local_10 = *local_1c;
        local_1c = local_1c + 1;
        bVar1 = (&DAT_004109f9)[local_10];
      }
      if (iStack_2c == 0) goto LAB_0040cff5;
    }
    if (local_10 == 0x68) {
      uStack_28 = uStack_28 | 4;
LAB_0040c9ad:
      local_10 = *local_1c;
      pbVar4 = local_1c + 1;
    }
    else if (local_10 == 0x6c) {
      uStack_28 = uStack_28 | 8;
      local_10 = *local_1c;
      pbVar4 = local_1c + 1;
      if (local_10 == 0x6c) {
        uStack_28 = uVar3 | 0x20;
        local_10 = local_1c[1];
        pbVar4 = local_1c + 2;
      }
    }
    else {
      pbVar4 = local_1c;
      if (local_10 == 0x4c) {
        uStack_28 = uStack_28 | 0x10;
        goto LAB_0040c9ad;
      }
    }
    local_1c = pbVar4;
    if ((local_10 != 0x25) && ((uStack_28 & 2) == 0)) {
      plStack_20 = (longlong *)*param_5;
      param_5 = param_5 + 1;
    }
    if (((local_10 != 0x5b) && (local_10 != 99)) && (local_10 != 0x6e)) {
      bVar1 = (&DAT_004109f9)[local_24];
      while ((bVar1 & 8) != 0) {
        local_34 = local_34 + 1;
        local_24 = (*local_14)(local_18);
        bVar1 = (&DAT_004109f9)[local_24];
      }
    }
    if (local_10 != 0x69) {
      if (local_10 == 0x62) {
        if (local_24 == 0x2d) {
          uStack_28 = uStack_28 | 1;
LAB_0040cc9a:
          local_34 = local_34 + 1;
          local_24 = (*local_14)(local_18);
          iStack_2c = iStack_2c + -1;
        }
        else if (local_24 == 0x2b) goto LAB_0040cc9a;
        iVar5 = 2;
        goto LAB_0040cb50;
      }
      if (local_10 == 0x6f) {
        if (local_24 == 0x2d) {
          uStack_28 = uStack_28 | 1;
LAB_0040ccd0:
          local_34 = local_34 + 1;
          local_24 = (*local_14)(local_18);
          iStack_2c = iStack_2c + -1;
        }
        else if (local_24 == 0x2b) goto LAB_0040ccd0;
LAB_0040cce7:
        iVar5 = 8;
        goto LAB_0040cb50;
      }
      if ((local_10 == 100) || (local_10 == 0x75)) {
        if (local_24 == 0x2d) {
          uStack_28 = uStack_28 | 1;
LAB_0040cd06:
          local_34 = local_34 + 1;
          local_24 = (*local_14)(local_18);
          iStack_2c = iStack_2c + -1;
        }
        else if (local_24 == 0x2b) goto LAB_0040cd06;
        goto LAB_0040cd1d;
      }
      if (((local_10 == 0x78) || (local_10 == 0x70)) || (local_10 == 0x58)) {
        if (local_24 == 0x2d) {
          uStack_28 = uStack_28 | 1;
LAB_0040cd3c:
          local_34 = local_34 + 1;
          local_24 = (*local_14)(local_18);
          iStack_2c = iStack_2c + -1;
        }
        else if (local_24 == 0x2b) goto LAB_0040cd3c;
        iVar5 = 0x10;
        goto LAB_0040cb50;
      }
      if (local_10 == 0x73) {
        bVar1 = (&DAT_004109f9)[local_24];
        while ((bVar1 & 8) != 0) {
          local_34 = local_34 + 1;
          local_24 = (*local_14)(local_18);
          bVar1 = (&DAT_004109f9)[local_24];
        }
        if (local_24 == 0xffffffff) goto LAB_0040cba5;
        if ((uStack_28 & 2) == 0) {
          local_30 = local_30 + 1;
        }
        do {
          iVar5 = iStack_2c + -1;
          bVar8 = iStack_2c == 0;
          iStack_2c = iVar5;
          if (((bVar8) || (local_24 == 0x20)) || ((local_24 == 10 || (local_24 == 9)))) break;
          if ((uStack_28 & 2) == 0) {
            *(undefined1 *)plStack_20 = (undefined1)local_24;
            plStack_20 = (longlong *)((int)plStack_20 + 1);
          }
          local_34 = local_34 + 1;
          local_24 = (*local_14)(local_18);
        } while (local_24 != 0xffffffff);
        if ((uStack_28 & 2) == 0) {
          *(undefined1 *)plStack_20 = 0;
        }
LAB_0040cdfa:
        local_34 = local_34 + -1;
        (*(code *)param_1)(local_24,local_18);
        goto LAB_0040c865;
      }
      if (local_10 != 99) {
        if ((((local_10 == 0x65) || (local_10 == 0x45)) || (local_10 == 0x66)) ||
           ((local_10 == 0x67 || (local_10 == 0x47)))) {
          if (DAT_00416370 == (code *)0x0) {
            FUN_0040dff0(s_Floating_point_not_loaded_00410afc);
          }
        }
        else if (local_10 != 0x5b) {
          if (local_10 != 0x6e) {
            if (local_10 != 0x25) {
              if (local_10 == 0) {
                return local_30;
              }
              goto LAB_0040cff5;
            }
            goto LAB_0040cc41;
          }
          local_34 = local_34 + -1;
          (*(code *)param_1)(local_24,local_18);
          if (local_24 == 0xffffffff) {
            local_34 = local_34 + 1;
          }
          if ((uStack_28 & 2) == 0) {
            *(int *)plStack_20 = local_34;
          }
          goto LAB_0040c865;
        }
        if (local_10 == 0x5b) {
          iVar5 = FUN_0040d010(&local_34);
        }
        else {
          iVar5 = (*DAT_00416370)(&local_34);
        }
        if (iVar5 != 0) goto LAB_0040cdfa;
LAB_0040cb9a:
        if (local_24 != 0xffffffff) goto LAB_0040cff5;
        goto LAB_0040cba5;
      }
      if (iStack_2c == 0x7fff) {
        iStack_2c = 1;
      }
      bVar8 = false;
      local_10 = 99;
      while (bVar7 = iStack_2c != 0, iStack_2c = iStack_2c + -1, bVar7) {
        if (local_24 == 0xffffffff) {
          return local_30;
        }
        bVar8 = true;
        if ((uStack_28 & 2) == 0) {
          *(undefined1 *)plStack_20 = (undefined1)local_24;
          plStack_20 = (longlong *)((int)plStack_20 + 1);
        }
        if (iStack_2c != 0) {
          local_34 = local_34 + 1;
          local_24 = (*local_14)(local_18);
        }
      }
      if (!bVar8) goto LAB_0040cc31;
      if ((uStack_28 & 2) == 0) {
LAB_0040cc28:
        local_30 = local_30 + 1;
      }
      goto LAB_0040c865;
    }
    if (local_24 == 0x2d) {
      uStack_28 = uStack_28 | 1;
LAB_0040cae0:
      local_34 = local_34 + 1;
      local_24 = (*local_14)(local_18);
      iStack_2c = iStack_2c + -1;
    }
    else if (local_24 == 0x2b) goto LAB_0040cae0;
    if (local_24 == 0x30) {
      local_34 = local_34 + 1;
      local_24 = (*local_14)(local_18);
      if ((local_24 == 0x62) || (local_24 == 0x42)) {
        iVar5 = 2;
      }
      else {
        if ((local_24 != 0x78) && (local_24 != 0x58)) {
          if (((int)local_24 < 0x31) || (0x38 < (int)local_24)) {
            local_34 = local_34 + -1;
            (*(code *)param_1)(local_24,local_18);
            local_24 = 0x30;
          }
          goto LAB_0040cce7;
        }
        iVar5 = 0x10;
      }
      local_34 = local_34 + 1;
      local_24 = (*local_14)(local_18);
    }
    else {
LAB_0040cd1d:
      iVar5 = 10;
    }
LAB_0040cb50:
    lVar9 = 0;
    bVar8 = false;
    iStack_c = 1;
    iVar6 = iStack_2c + -1;
    bVar7 = iStack_2c != 0;
    iStack_2c = iVar6;
    lVar2 = 0;
    if (bVar7) {
      iStack_4 = 0;
      iStack_8 = iVar5;
      while (iStack_38 = (int)((ulonglong)lVar9 >> 0x20), lVar2 = lVar9,
            ((&DAT_004109f9)[local_24] & 0x80) != 0) {
        iStack_c = 0;
        if ((int)local_24 < 0x61) {
          if ((int)local_24 < 0x41) {
            iVar6 = local_24 - 0x30;
          }
          else {
            iVar6 = local_24 - 0x37;
          }
        }
        else {
          iVar6 = local_24 - 0x57;
        }
        if ((iVar6 < 0) || (iVar5 <= iVar6)) goto LAB_0040cbb6;
        bVar8 = true;
        lVar9 = FUN_0040e030(iStack_38,iStack_4);
        lVar9 = lVar9 - iVar6;
        local_34 = local_34 + 1;
        local_24 = (*local_14)(local_18);
        iVar6 = iStack_2c + -1;
        bVar7 = iStack_2c == 0;
        iStack_2c = iVar6;
        lVar2 = lVar9;
        if (bVar7) goto LAB_0040cbb6;
      }
      if (iStack_c != 0) goto LAB_0040cb9a;
    }
LAB_0040cbb6:
    iStack_38 = (int)((ulonglong)lVar2 >> 0x20);
    iStack_3c = (int)lVar2;
    local_34 = local_34 + -1;
    (*(code *)param_1)(local_24,local_18);
    if (bVar8) {
      if ((uStack_28 & 2) == 0) {
        if ((uStack_28 & 1) == 0) {
          lVar2 = CONCAT44(-(uint)(iStack_3c != 0) - iStack_38,-iStack_3c);
        }
        iStack_3c = (int)lVar2;
        if ((uStack_28 & 4) == 0) {
          if (((uStack_28 & 8) == 0) && ((uStack_28 & 0x20) != 0)) {
            *plStack_20 = lVar2;
          }
          else {
            *(int *)plStack_20 = iStack_3c;
          }
        }
        else {
          iStack_3c._0_2_ = (undefined2)lVar2;
          *(undefined2 *)plStack_20 = (undefined2)iStack_3c;
        }
        goto LAB_0040cc28;
      }
    }
    else {
LAB_0040cc31:
      if (local_24 == 0xffffffff) {
LAB_0040cba5:
        if (local_30 == 0) {
          local_30 = -1;
        }
        return local_30;
      }
    }
  } while( true );
}



/* Address: 0040d010 */

int FUN_0040d010(int *param_1)

{
  int *piVar1;
  byte bVar2;
  int iVar3;
  bool bVar4;
  bool bVar5;
  int local_124;
  char local_11b [256];
  byte local_1b;
  byte local_1a;
  byte local_19;
  byte local_18;
  byte *local_14;
  int *local_10;
  int *local_c;
  int *local_8;
  int *local_4;
  
  local_19 = 0;
  local_18 = 0;
  local_4 = param_1 + 6;
  local_1a = *(byte *)*local_4;
  *local_4 = *local_4 + 1;
  bVar4 = local_1a != 0x5e;
  if (!bVar4) {
    local_1a = *(byte *)*local_4;
    *local_4 = *local_4 + 1;
  }
  bVar5 = local_1a == 0x2d;
  local_1b = local_1a;
  FUN_0040d2a4(local_11b,!bVar4,0x100);
  piVar1 = local_4;
  do {
    if ((local_1b == 0x2d) && (!bVar5)) {
      local_19 = local_1a;
      local_1b = *(byte *)*piVar1;
      *piVar1 = *piVar1 + 1;
      local_18 = local_1b;
      if (local_1b == 0x5d) break;
    }
    local_1a = local_1b;
    local_11b[local_1b] = bVar4;
    local_1b = local_18;
    if ((char)local_19 < (char)local_18) {
      local_1b = local_19;
    }
    bVar2 = local_18;
    if ((char)local_18 < (char)local_19) {
      bVar2 = local_19;
    }
    if ((char)local_1b <= (char)bVar2) {
      do {
        local_11b[local_1b] = bVar4;
        local_1b = local_1b + 1;
        bVar2 = local_18;
        if ((char)local_18 < (char)local_19) {
          bVar2 = local_19;
        }
      } while ((char)local_1b <= (char)bVar2);
    }
    local_1b = *(byte *)*piVar1;
    *piVar1 = *piVar1 + 1;
    local_18 = local_1b;
    local_19 = local_1b;
    bVar5 = false;
  } while (local_1b != 0x5d);
  local_124 = 0;
  iVar3 = param_1[2];
  param_1[2] = param_1[2] + -1;
  if ((iVar3 != 0) && (local_11b[param_1[4]] != '\0')) {
    local_14 = (byte *)(param_1 + 3);
    piVar1 = param_1 + 5;
    local_124 = 1;
    local_10 = param_1 + 8;
    local_c = param_1 + 7;
    local_8 = param_1 + 2;
    local_1b = local_18;
    do {
      if ((*local_14 & 2) == 0) {
        *(char *)*piVar1 = (char)param_1[4];
        *(undefined1 *)(*piVar1 + 1) = 0;
        *piVar1 = *piVar1 + 1;
      }
      *param_1 = *param_1 + 1;
      iVar3 = (*(code *)*local_10)(*local_c);
      param_1[4] = iVar3;
      iVar3 = *local_8;
      *local_8 = *local_8 + -1;
    } while ((iVar3 != 0) && (local_11b[param_1[4]] != '\0'));
  }
  if ((*(byte *)(param_1 + 3) & 2) == 0) {
    param_1[1] = param_1[1] + local_124;
  }
  return local_124;
}



/* Address: 0040d270 */

void __fastcall FUN_0040d270(uint param_1)

{
  undefined4 in_EAX;
  uint uVar1;
  undefined4 *unaff_EDI;
  
  for (uVar1 = param_1 >> 3; uVar1 != 0; uVar1 = uVar1 - 1) {
    *unaff_EDI = in_EAX;
    unaff_EDI[1] = in_EAX;
    unaff_EDI[2] = in_EAX;
    unaff_EDI[3] = in_EAX;
    unaff_EDI[4] = in_EAX;
    unaff_EDI[5] = in_EAX;
    unaff_EDI[6] = in_EAX;
    unaff_EDI[7] = in_EAX;
    unaff_EDI = unaff_EDI + 8;
  }
  for (uVar1 = param_1 & 7; uVar1 != 0; uVar1 = uVar1 - 1) {
    *unaff_EDI = in_EAX;
    unaff_EDI = unaff_EDI + 1;
  }
  return;
}



/* Address: 0040d2a4 */

undefined1 * __cdecl FUN_0040d2a4(undefined1 *param_1,undefined1 param_2,uint param_3)

{
  uint uVar1;
  uint extraout_EDX;
  undefined1 *puVar2;
  
  puVar2 = param_1;
  if (7 < param_3) {
    uVar1 = (uint)param_1 & 3;
    while (uVar1 != 0) {
      *puVar2 = param_2;
      puVar2 = puVar2 + 1;
      param_3 = param_3 - 1;
      uVar1 = (uint)puVar2 & 3;
    }
    param_2 = FUN_0040d270(param_3 >> 2);
    param_3 = extraout_EDX;
  }
  for (; param_3 != 0; param_3 = param_3 - 1) {
    *puVar2 = param_2;
    puVar2 = puVar2 + 1;
  }
  return param_1;
}



/* Address: 0040d2fc */

char * __cdecl FUN_0040d2fc(uint *param_1,char param_2)

{
  char *pcVar1;
  char *pcVar2;
  bool bVar3;
  
  pcVar1 = FUN_0040c63c(param_1);
  pcVar2 = pcVar1 + (int)param_1;
  do {
    if (*pcVar2 == param_2) {
      return (char *)((int)param_1 + (int)pcVar1);
    }
    pcVar2 = pcVar2 + -1;
    bVar3 = pcVar1 != (char *)0x0;
    pcVar1 = pcVar1 + -1;
  } while (bVar3);
  return (char *)0x0;
}



/* Address: 0040d333 */

void FUN_0040d333(void)

{
  uint in_EAX;
  bool bVar1;
  
  do {
    bVar1 = 0xfff < in_EAX;
    in_EAX = in_EAX - 0x1000;
  } while (bVar1);
                    /* WARNING: Treating indirect jump as return */
  return;
}



/* Address: 0040d350 */

char * __cdecl FUN_0040d350(char *param_1)

{
  char cVar1;
  int iVar2;
  char *pcVar3;
  
  cVar1 = *param_1;
  pcVar3 = param_1;
  while (cVar1 != '\0') {
    iVar2 = FUN_0040c708((int)*pcVar3);
    *pcVar3 = (char)iVar2;
    pcVar3 = pcVar3 + 1;
    cVar1 = *pcVar3;
  }
  return param_1;
}



/* Address: 0040d378 */

uint * __cdecl FUN_0040d378(uint *param_1,uint *param_2)

{
  char *pcVar1;
  char *pcVar2;
  uint *puVar3;
  uint uVar4;
  uint local_408 [256];
  uint *local_8;
  uint *local_4;
  
  pcVar1 = FUN_0040c63c(param_2);
  pcVar2 = FUN_0040c63c(param_1);
  if (pcVar1 == (char *)0x0) {
    return param_1;
  }
  if (pcVar1 <= pcVar2) {
    FUN_0040e150(local_408,pcVar1,0x100);
    puVar3 = param_2;
    do {
      local_408[(byte)*puVar3] = (uint)(pcVar1 + (-1 - ((int)puVar3 - (int)param_2)));
      puVar3 = (uint *)((int)puVar3 + 1);
    } while (*(byte *)puVar3 != 0);
    local_4 = (uint *)((char *)((int)param_2 + (int)pcVar1) + -1);
    puVar3 = (uint *)((char *)((int)param_1 + (int)pcVar1) + -1);
    local_8 = local_4;
    while( true ) {
      for (; (byte)*puVar3 == (byte)*local_4; puVar3 = (uint *)((int)puVar3 + -1)) {
        if (local_4 == param_2) {
          return puVar3;
        }
        local_4 = (uint *)((int)local_4 + -1);
      }
      uVar4 = (int)pcVar1 - ((int)local_4 - (int)param_2);
      if (uVar4 < local_408[(byte)*puVar3]) {
        uVar4 = local_408[(byte)*puVar3];
      }
      if (pcVar2 <= (char *)((int)puVar3 + (uVar4 - (int)param_1))) break;
      puVar3 = (uint *)((int)puVar3 + uVar4);
      local_4 = local_8;
    }
  }
  return (uint *)0x0;
}



/* Address: 0040d480 */

void entry(void)

{
  HMODULE pHVar1;
  WPARAM WVar2;
  undefined4 *unaff_FS_OFFSET;
  undefined4 uVar3;
  LPSTR pCVar4;
  undefined4 uVar5;
  undefined4 local_14;
  code *pcStack_10;
  undefined *puStack_c;
  undefined4 local_8;
  
  puStack_c = &DAT_00410b1c;
  pcStack_10 = FUN_0040e40c;
  local_14 = *unaff_FS_OFFSET;
  *unaff_FS_OFFSET = &local_14;
  local_8 = 0;
  FUN_0040e500();
  FUN_0040e534();
  FUN_0040e7c0();
  DAT_00410e28 = GetCommandLineA();
  FUN_0040e800();
  FUN_0040e8e0();
  FUN_0040e8f0();
  FUN_0040e170();
  uVar5 = 10;
  uVar3 = 0;
  pCVar4 = DAT_00410e28;
  pHVar1 = GetModuleHandleA((LPCSTR)0x0);
  WVar2 = FUN_0040bfec(pHVar1,uVar3,pCVar4,uVar5);
  FUN_0040e274(WVar2);
  *unaff_FS_OFFSET = local_14;
  return;
}



/* Address: 0040d560 */

void FUN_0040d560(void)

{
  return;
}



/* Address: 0040d564 */

void __cdecl FUN_0040d564(LPCSTR param_1,uint param_2,uint param_3)

{
  FUN_0040d57c(param_1,param_2,0x40,param_3);
  return;
}



/* Address: 0040d57c */

int __cdecl FUN_0040d57c(LPCSTR param_1,uint param_2,uint param_3,uint param_4)

{
  bool bVar1;
  uint uVar2;
  HANDLE pvVar3;
  undefined3 extraout_var;
  uint uVar4;
  uint extraout_ECX;
  undefined4 extraout_ECX_00;
  undefined4 uVar5;
  undefined4 extraout_EDX;
  undefined4 extraout_EDX_00;
  DWORD local_1c;
  DWORD local_18;
  int local_14;
  DWORD local_10;
  DWORD local_c;
  
  uVar4 = param_3 & 0x70;
  if ((param_2 & 2) == 0) {
    if ((param_2 & 1) == 0) {
      local_10 = 0x80000000;
    }
    else {
      local_10 = 0x40000000;
    }
  }
  else {
    local_10 = 0xc0000000;
  }
  uVar2 = param_2 & 0x80 | uVar4;
  if ((uVar2 == 0) || (uVar2 == 0x10)) {
    local_c = 0;
  }
  else if (uVar2 == 0x20) {
    local_c = 1;
  }
  else if (uVar2 == 0x30) {
    local_c = 2;
  }
  else if (uVar2 == 0x40) {
    local_c = 3;
  }
  if ((~DAT_00410e5c & param_4 & 0x80) == 0) {
    local_1c = 1;
  }
  else {
    local_1c = 0x80;
  }
  if ((param_2 & 0x100) == 0) {
    if ((param_2 & 0x200) == 0) {
      local_18 = 3;
    }
    else {
      local_18 = 5;
    }
  }
  else if ((param_2 & 0x400) == 0) {
    if ((param_2 & 0x200) == 0) {
      local_18 = 4;
    }
    else {
      local_18 = 2;
    }
  }
  else {
    local_18 = 1;
  }
  uVar5 = 3;
  LOCK();
  DAT_00416386 = DAT_00416386 + 1;
  UNLOCK();
  if (DAT_00416386 != 0) {
    FUN_0040e590(3);
    uVar4 = extraout_ECX;
    uVar5 = extraout_EDX;
  }
  local_14 = 3;
  do {
    if (local_14 == 0x3c) {
      FUN_0040ebd4(uVar4,uVar5,0x18);
      local_14 = -1;
LAB_0040d741:
      LOCK();
      DAT_00416386 = DAT_00416386 + -1;
      UNLOCK();
      if (-1 < DAT_00416386) {
        FUN_0040e60c(3);
      }
      return local_14;
    }
    if (*(int *)(&DAT_00410b64 + local_14 * 4) == 0) {
      pvVar3 = CreateFileA(param_1,local_10,local_c,(LPSECURITY_ATTRIBUTES)0x0,local_18,local_1c,
                           (HANDLE)0x0);
      (&DAT_00410b28)[local_14] = 0;
      if (pvVar3 == (HANDLE)0xffffffff) {
        FUN_0040ebfc(extraout_ECX_00,extraout_EDX_00);
        local_14 = -1;
      }
      else {
        *(HANDLE *)(&DAT_00410b64 + local_14 * 4) = pvVar3;
        if ((param_2 & 8) != 0) {
          (&DAT_00410b28)[local_14] = (&DAT_00410b28)[local_14] | 4;
        }
        bVar1 = FUN_0040ebb4(local_14);
        if (CONCAT31(extraout_var,bVar1) != 0) {
          (&DAT_00410b28)[local_14] = (&DAT_00410b28)[local_14] | 8;
        }
        if (((param_2 & 0x4000) != 0) || (((param_2 & 0x8000) == 0 && ((DAT_00416375 & 0x80) == 0)))
           ) {
          (&DAT_00410b28)[local_14] = (&DAT_00410b28)[local_14] | 0x10;
        }
      }
      goto LAB_0040d741;
    }
    local_14 = local_14 + 1;
  } while( true );
}



/* Address: 0040d764 */

undefined4 __fastcall FUN_0040d764(undefined4 param_1,undefined4 param_2,int param_3)

{
  HANDLE hObject;
  BOOL BVar1;
  undefined4 extraout_ECX;
  undefined4 extraout_EDX;
  
  if ((-1 < param_3) && (param_3 < 0x3c)) {
    hObject = *(HANDLE *)(&DAT_00410b64 + param_3 * 4);
    (&DAT_00410b28)[param_3] = 0;
    *(undefined4 *)(&DAT_00410b64 + param_3 * 4) = 0;
    BVar1 = CloseHandle(hObject);
    if (BVar1 != 0) {
      return 0;
    }
    FUN_0040ebfc(extraout_ECX,extraout_EDX);
    return 0xffffffff;
  }
  FUN_0040ebd4(param_1,param_2,9);
  return 0xffffffff;
}



/* Address: 0040d7b8 */

undefined4 __cdecl FUN_0040d7b8(LPCSTR param_1)

{
  BOOL BVar1;
  undefined4 extraout_ECX;
  undefined4 extraout_EDX;
  
  BVar1 = DeleteFileA(param_1);
  if (BVar1 != 0) {
    return 0;
  }
  FUN_0040ebfc(extraout_ECX,extraout_EDX);
  return 0xffffffff;
}



/* Address: 0040d7d4 */

DWORD __cdecl FUN_0040d7d4(int param_1,LONG param_2,DWORD param_3)

{
  DWORD DVar1;
  undefined4 extraout_ECX;
  undefined4 extraout_EDX;
  
  DVar1 = SetFilePointer(*(HANDLE *)(&DAT_00410b64 + param_1 * 4),param_2,(PLONG)0x0,param_3);
  if (DVar1 == 0xffffffff) {
    FUN_0040ebfc(extraout_ECX,extraout_EDX);
  }
  return DVar1;
}



/* Address: 0040d800 */

undefined4 __cdecl FUN_0040d800(int param_1,DWORD param_2)

{
  DWORD DVar1;
  BOOL BVar2;
  undefined4 extraout_ECX;
  undefined4 extraout_ECX_00;
  undefined4 uVar3;
  undefined4 extraout_EDX;
  undefined4 extraout_EDX_00;
  undefined4 uVar4;
  
  DVar1 = SetFilePointer(*(HANDLE *)(&DAT_00410b64 + param_1 * 4),param_2,(PLONG)0x0,0);
  uVar3 = extraout_ECX;
  uVar4 = extraout_EDX;
  if (DVar1 == param_2) {
    BVar2 = SetEndOfFile(*(HANDLE *)(&DAT_00410b64 + param_1 * 4));
    uVar3 = extraout_ECX_00;
    uVar4 = extraout_EDX_00;
    if (BVar2 != 0) {
      return 0;
    }
  }
  FUN_0040ebfc(uVar3,uVar4);
  return 0xffffffff;
}



/* Address: 0040d840 */

char * __fastcall FUN_0040d840(char *param_1,char *param_2,uint param_3,char *param_4,char *param_5)

{
  BOOL BVar1;
  DWORD DVar2;
  char *extraout_ECX;
  char *extraout_ECX_00;
  uint uVar3;
  int iVar4;
  int iVar5;
  short sVar6;
  uint uVar7;
  char *extraout_EDX;
  char *pcVar8;
  char *pcVar9;
  char *pcVar10;
  bool bVar11;
  undefined8 uVar12;
  char *local_1c;
  char local_18;
  uint local_14;
  char *local_10;
  char *local_c;
  char *local_8;
  
  if (0x3b < param_3) goto LAB_0040d8aa;
  if (param_5 == (char *)0x0) {
    return (char *)0x0;
  }
  pcVar10 = param_5;
  if (((&DAT_00410b28)[param_3] & 0x14) != 0) {
    bVar11 = ((&DAT_00410b28)[param_3] & 4) == 0;
    param_1 = param_5;
    if (bVar11) {
LAB_0040d8f9:
      uVar7 = 0;
      pcVar10 = param_4;
LAB_0040d907:
      if (param_1 != (char *)0x0) goto LAB_0040d907;
      goto code_r0x0040d909;
    }
    DVar2 = SetFilePointer(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),0,(PLONG)0x0,2);
    if ((short)DVar2 == -1) goto LAB_0040d8aa;
    bVar11 = ((&DAT_00410b28)[param_3] & 0x10) == 0;
    pcVar10 = param_1;
    if (!bVar11) goto LAB_0040d8f9;
  }
  goto LAB_0040d85f;
LAB_0040da25:
  if (iVar5 != 0) goto LAB_0040da25;
  goto code_r0x0040da27;
LAB_0040da25:
  iVar5 = iVar5 + -1;
  pcVar8 = pcVar10 + 1;
  bVar11 = *pcVar10 == '\n';
  pcVar10 = pcVar8;
  if (!bVar11) goto LAB_0040da25;
code_r0x0040da27:
  if (bVar11) {
    sVar6 = (short)iVar4 + -1;
    iVar4 = CONCAT22((short)((uint)iVar4 >> 0x10),sVar6);
    bVar11 = sVar6 == 0;
    goto LAB_0040da25;
  }
LAB_0040da2d:
  param_5 = param_5 + (-iVar4 - (int)local_8);
  if (param_5 == (char *)0x0) {
    FUN_0040ebd4(iVar5,iVar4,0x1c);
LAB_0040da4f:
    param_5 = (char *)0xffffffff;
  }
LAB_0040da59:
  GlobalFree(local_10);
  return param_5;
LAB_0040d907:
  param_1 = param_1 + -1;
  pcVar8 = pcVar10 + 1;
  bVar11 = *pcVar10 == '\n';
  pcVar10 = pcVar8;
  if (!bVar11) goto LAB_0040d907;
code_r0x0040d909:
  if (bVar11) {
    uVar7 = uVar7 + 1;
    bVar11 = uVar7 == 0;
    goto LAB_0040d907;
  }
  pcVar10 = param_5;
  if (uVar7 != 0) {
    param_2 = param_5 + uVar7;
    local_14 = uVar7;
    if ((!CARRY4((uint)param_5,uVar7)) &&
       (bVar11 = param_2 < (char *)0xfffffffc, param_2 = param_2 + 4, bVar11)) {
      uVar7 = (uint)param_2 & 0xfffffffc;
      local_10 = (char *)GlobalAlloc(0,uVar7);
      param_1 = extraout_ECX_00;
      param_2 = extraout_EDX;
      if (local_10 != (char *)0x0) {
        local_c = local_10 + (uVar7 - 1);
        bVar11 = local_c == (char *)0x0;
        local_18 = '\0';
        local_8 = param_5;
        pcVar8 = param_4;
        pcVar10 = local_10;
code_r0x0040d963:
        do {
          pcVar9 = pcVar8;
          if (local_8 != (char *)0x0) {
            local_8 = local_8 + -1;
            pcVar9 = pcVar8 + 1;
            bVar11 = *pcVar8 == '\n';
            pcVar8 = pcVar9;
            if (!bVar11) goto code_r0x0040d963;
          }
          if (!bVar11) {
            local_18 = '\x01';
          }
          uVar7 = (int)pcVar9 - (int)param_4;
          pcVar8 = pcVar10 + uVar7;
          if (local_c < pcVar8) {
            uVar7 = (int)local_c - (int)pcVar10;
            if (pcVar10 <= local_c && uVar7 != 0) goto LAB_0040d9a9;
LAB_0040d9b7:
            local_8 = local_8 + ((int)pcVar8 - (int)pcVar10);
          }
          else {
            if (local_18 == '\x01') {
LAB_0040d9a9:
              for (uVar3 = uVar7 >> 2; uVar3 != 0; uVar3 = uVar3 - 1) {
                *(undefined4 *)pcVar10 = *(undefined4 *)param_4;
                param_4 = param_4 + 4;
                pcVar10 = pcVar10 + 4;
              }
              for (uVar7 = uVar7 & 3; uVar7 != 0; uVar7 = uVar7 - 1) {
                *pcVar10 = *param_4;
                param_4 = param_4 + 1;
                pcVar10 = pcVar10 + 1;
              }
              goto LAB_0040d9b7;
            }
            for (uVar3 = uVar7 >> 2; uVar3 != 0; uVar3 = uVar3 - 1) {
              *(undefined4 *)pcVar10 = *(undefined4 *)param_4;
              param_4 = param_4 + 4;
              pcVar10 = pcVar10 + 4;
            }
            for (uVar7 = uVar7 & 3; uVar7 != 0; uVar7 = uVar7 - 1) {
              *pcVar10 = *param_4;
              param_4 = param_4 + 1;
              pcVar10 = pcVar10 + 1;
            }
            pcVar10[-0xffffffff00000001] = '\r';
            pcVar10[0] = '\n';
            pcVar10 = pcVar10 + 1;
            bVar11 = local_8 == (char *)0x0;
            pcVar8 = param_4;
            if (!bVar11) goto code_r0x0040d963;
          }
          DVar2 = (int)pcVar10 - (int)local_10;
          if ((param_3 < 3) && (DAT_00410c54 != (code *)0x0)) {
            pcVar10 = (char *)(*DAT_00410c54)(param_3,local_10,DVar2,local_10);
          }
          else {
            pcVar8 = local_10;
            BVar1 = WriteFile(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),local_10,DVar2,
                              (LPDWORD)&local_1c,(LPOVERLAPPED)0x0);
            pcVar10 = local_1c;
            if (BVar1 == 0) {
              FUN_0040ebd4(DVar2,pcVar8,9);
              goto LAB_0040da4f;
            }
          }
          iVar4 = DVar2 - (int)pcVar10;
          if (iVar4 != 0) {
            pcVar10 = local_10 + (int)pcVar10 + 1;
            iVar5 = iVar4 + -1;
            bVar11 = iVar5 == 0;
            if (!bVar11) goto LAB_0040da25;
            goto LAB_0040da2d;
          }
          bVar11 = local_8 == (char *)0x0;
          pcVar8 = param_4;
          pcVar10 = local_10;
        } while (!bVar11);
        goto LAB_0040da59;
      }
    }
    goto LAB_0040d8aa;
  }
LAB_0040d85f:
  if ((param_3 < 3) && (DAT_00410c54 != (code *)0x0)) {
    uVar12 = (*DAT_00410c54)(param_3,param_4,pcVar10);
    param_1 = extraout_ECX;
  }
  else {
    param_2 = param_4;
    param_1 = pcVar10;
    BVar1 = WriteFile(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),param_4,(DWORD)pcVar10,
                      (LPDWORD)&local_1c,(LPOVERLAPPED)0x0);
    uVar12 = CONCAT44(param_2,local_1c);
    if (BVar1 == 0) {
LAB_0040d8aa:
      FUN_0040ebd4(param_1,param_2,9);
      return (char *)0xffffffff;
    }
  }
  if ((char *)uVar12 == (char *)0x0) {
    FUN_0040ebd4(param_1,(int)((ulonglong)uVar12 >> 0x20),0x1c);
    return (char *)0xffffffff;
  }
  return (char *)uVar12;
}



/* Address: 0040da70 */

DWORD __fastcall FUN_0040da70(DWORD param_1,char *param_2,uint param_3,char *param_4,DWORD param_5)

{
  BOOL BVar1;
  DWORD DVar2;
  int iVar3;
  uint uVar4;
  DWORD extraout_ECX;
  DWORD extraout_ECX_00;
  char *extraout_EDX;
  char *pcVar5;
  char *extraout_EDX_00;
  char *lpBuffer;
  char *pcVar6;
  char *pcVar7;
  char *pcVar8;
  bool bVar9;
  DWORD local_c;
  char *local_8;
  
  if (param_3 < 0x3c) {
    if (param_5 == 0) {
      return 0;
    }
    if (((&DAT_00410b28)[param_3] & 0x10) != 0) {
      (&DAT_00410b28)[param_3] = (&DAT_00410b28)[param_3] & 0xfe;
      lpBuffer = param_4;
LAB_0040dada:
      pcVar5 = lpBuffer;
      BVar1 = ReadFile(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),lpBuffer,param_5,&local_c,
                       (LPOVERLAPPED)0x0);
      if (BVar1 == 0) goto LAB_0040dba9;
      if (local_c != param_5) {
        (&DAT_00410b28)[param_3] = (&DAT_00410b28)[param_3] | 1;
        param_5 = local_c;
      }
      bVar9 = param_5 == 0;
      pcVar8 = lpBuffer;
      if (!bVar9) {
        do {
          pcVar6 = pcVar8;
          if (param_5 == 0) break;
          param_5 = param_5 - 1;
          pcVar6 = pcVar8 + 1;
          bVar9 = *pcVar8 == '\x1a';
          pcVar8 = pcVar6;
        } while (!bVar9);
        pcVar8 = pcVar5 + local_c;
        if (bVar9) {
          pcVar8 = pcVar6 + -1;
          if ((((&DAT_00410b28)[param_3] & 8) == 0) &&
             (DVar2 = SetFilePointer(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),
                                     (int)(pcVar5 + local_c) - (int)pcVar8,(PLONG)0x0,1),
             param_5 = extraout_ECX, pcVar5 = extraout_EDX, (short)DVar2 == -1)) goto LAB_0040dba9;
          (&DAT_00410b28)[param_3] = (&DAT_00410b28)[param_3] | 1;
        }
        iVar3 = (int)pcVar8 - (int)lpBuffer;
        bVar9 = true;
LAB_0040db26:
        do {
          if (iVar3 != 0) {
            iVar3 = iVar3 + -1;
            pcVar5 = lpBuffer + 1;
            bVar9 = *lpBuffer == '\r';
            lpBuffer = pcVar5;
            if (!bVar9) goto LAB_0040db26;
          }
          if (!bVar9) break;
          pcVar6 = (char *)0x0;
          pcVar7 = lpBuffer;
          if (pcVar8 <= lpBuffer) goto LAB_0040dbbd;
          bVar9 = *lpBuffer == '\n';
          if (bVar9) {
            local_8 = lpBuffer + -1;
            do {
              iVar3 = (int)pcVar8 - (int)pcVar7;
              bVar9 = iVar3 == 0;
LAB_0040db49:
              do {
                if (iVar3 != 0) {
                  iVar3 = iVar3 + -1;
                  pcVar5 = pcVar7 + 1;
                  bVar9 = *pcVar7 == '\r';
                  pcVar7 = pcVar5;
                  if (!bVar9) goto LAB_0040db49;
                }
                pcVar6 = lpBuffer;
                if (!bVar9) goto LAB_0040dc1a;
                if (pcVar8 <= pcVar7) goto LAB_0040dbbd;
                bVar9 = *pcVar7 == '\n';
              } while (!bVar9);
              pcVar5 = lpBuffer;
              for (uVar4 = (uint)(pcVar7 + (-1 - (int)lpBuffer)) >> 2; uVar4 != 0; uVar4 = uVar4 - 1
                  ) {
                *(undefined4 *)local_8 = *(undefined4 *)pcVar5;
                pcVar5 = pcVar5 + 4;
                local_8 = local_8 + 4;
              }
              for (uVar4 = (uint)(pcVar7 + (-1 - (int)lpBuffer)) & 3; uVar4 != 0; uVar4 = uVar4 - 1)
              {
                *local_8 = *pcVar5;
                pcVar5 = pcVar5 + 1;
                local_8 = local_8 + 1;
              }
              lpBuffer = pcVar5 + 1;
            } while( true );
          }
        } while( true );
      }
LAB_0040dc50:
      return (int)lpBuffer - (int)param_4;
    }
    param_2 = param_4;
    param_1 = param_5;
    BVar1 = ReadFile(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),param_4,param_5,&local_c,
                     (LPOVERLAPPED)0x0);
    if (BVar1 != 0) {
      return local_c;
    }
  }
  FUN_0040ebd4(param_1,param_2,9);
  return 0xffffffff;
LAB_0040dbbd:
  if (((&DAT_00410b28)[param_3] & 1) == 0) {
    pcVar5 = pcVar8 + -1;
    param_5 = 1;
    BVar1 = ReadFile(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),pcVar5,1,&local_c,(LPOVERLAPPED)0x0);
    if (BVar1 == 0) {
LAB_0040dba9:
      FUN_0040ebd4(param_5,pcVar5,9);
      return 0xffffffff;
    }
    if (local_c == 0) {
      (&DAT_00410b28)[param_3] = (&DAT_00410b28)[param_3] | 1;
    }
    else if (pcVar7[-1] != '\n') {
      pcVar7[-1] = '\r';
      DVar2 = SetFilePointer(*(HANDLE *)(&DAT_00410b64 + param_3 * 4),-1,(PLONG)0x0,1);
      param_5 = extraout_ECX_00;
      pcVar5 = extraout_EDX_00;
      if ((short)DVar2 == -1) goto LAB_0040dba9;
    }
  }
LAB_0040dc1a:
  lpBuffer = pcVar7;
  if (pcVar6 == (char *)0x0) goto LAB_0040dc50;
  pcVar5 = pcVar6;
  pcVar8 = local_8;
  for (uVar4 = (uint)((int)lpBuffer - (int)pcVar6) >> 2; uVar4 != 0; uVar4 = uVar4 - 1) {
    *(undefined4 *)pcVar8 = *(undefined4 *)pcVar5;
    pcVar5 = pcVar5 + 4;
    pcVar8 = pcVar8 + 4;
  }
  uVar4 = (int)lpBuffer - (int)pcVar6 & 3;
  lpBuffer = pcVar8;
  for (; uVar4 != 0; uVar4 = uVar4 - 1) {
    *lpBuffer = *pcVar5;
    pcVar5 = pcVar5 + 1;
    lpBuffer = lpBuffer + 1;
  }
  if (((((&DAT_00410b28)[param_3] & 8) != 0) ||
      (param_5 = (int)pcVar5 - (int)lpBuffer, param_5 == 0)) ||
     (((&DAT_00410b28)[param_3] & 1) != 0)) goto LAB_0040dc50;
  goto LAB_0040dada;
}



/* Address: 0040dc60 */

char * __cdecl FUN_0040dc60(char *param_1,char *param_2)

{
  char cVar1;
  uint uVar2;
  int iVar3;
  uint uVar4;
  char *pcVar5;
  char *pcVar6;
  char *pcVar7;
  
  uVar2 = 0xffffffff;
  do {
    pcVar5 = param_2;
    if (uVar2 == 0) break;
    uVar2 = uVar2 - 1;
    pcVar5 = param_2 + 1;
    cVar1 = *param_2;
    param_2 = pcVar5;
  } while (cVar1 != '\0');
  uVar2 = ~uVar2;
  iVar3 = -1;
  pcVar7 = param_1;
  do {
    pcVar6 = pcVar7;
    if (iVar3 == 0) break;
    iVar3 = iVar3 + -1;
    pcVar6 = pcVar7 + 1;
    cVar1 = *pcVar7;
    pcVar7 = pcVar6;
  } while (cVar1 != '\0');
  pcVar5 = pcVar5 + -uVar2;
  pcVar7 = pcVar6 + -1;
  for (uVar4 = uVar2 >> 2; uVar4 != 0; uVar4 = uVar4 - 1) {
    *(undefined4 *)pcVar7 = *(undefined4 *)pcVar5;
    pcVar5 = pcVar5 + 4;
    pcVar7 = pcVar7 + 4;
  }
  for (uVar2 = uVar2 & 3; uVar2 != 0; uVar2 = uVar2 - 1) {
    *pcVar7 = *pcVar5;
    pcVar5 = pcVar5 + 1;
    pcVar7 = pcVar7 + 1;
  }
  return param_1;
}



/* Address: 0040dc9c */

int __cdecl FUN_0040dc9c(int *param_1)

{
  int iVar1;
  int iVar2;
  int iVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  int *piVar7;
  int local_28 [4];
  int local_18;
  int local_14;
  int local_4;
  
  iVar5 = -1;
  local_4 = param_1[5];
  if (0x7b1 < local_4) {
    iVar6 = local_4 + -0x7b2;
    iVar5 = (uint)*(ushort *)(&DAT_00410c58 + (param_1[4] + -1) * 2) + param_1[3] + -1;
    if ((local_4 % 4 == 0) && (1 < param_1[4] + -1)) {
      iVar5 = (uint)*(ushort *)(&DAT_00410c58 + (param_1[4] + -1) * 2) + param_1[3];
    }
    iVar1 = local_4 + -0x7b1;
    iVar2 = param_1[2];
    iVar3 = param_1[1];
    iVar4 = *param_1;
    FUN_0040def0();
    iVar5 = (iVar6 * 0x16d + (iVar1 >> 2) + iVar5) * 0x15180 + (iVar2 * 0x3c + iVar3) * 0x3c + iVar4
            + DAT_00410efc;
    if (DAT_00410f04 != 0) {
      piVar7 = local_28;
      for (iVar6 = 9; iVar6 != 0; iVar6 = iVar6 + -1) {
        *piVar7 = *param_1;
        param_1 = param_1 + 1;
        piVar7 = piVar7 + 1;
      }
      local_18 = local_18 + -1;
      local_14 = local_14 + -0x76c;
      iVar6 = FUN_0040dd58(local_28);
      iVar5 = iVar5 - iVar6;
    }
  }
  return iVar5;
}



/* Address: 0040dd58 */

undefined4 __cdecl FUN_0040dd58(int *param_1)

{
  int iVar1;
  int iVar2;
  undefined4 uVar3;
  int iVar4;
  
  iVar1 = param_1[4];
  if ((iVar1 < 3) || (9 < iVar1)) {
LAB_0040ddd4:
    uVar3 = 0;
  }
  else {
    if ((iVar1 < 4) || (8 < iVar1)) {
      iVar1 = FUN_0040deb0((int)param_1);
      iVar4 = iVar1 * 0x15180 + param_1[2] * 0xe10 + param_1[1] * 0x3c + *param_1;
      iVar1 = FUN_0040dddc(3,(int)param_1);
      iVar2 = FUN_0040de48(9,(int)param_1);
      if ((iVar4 < iVar1 * 0x15180 + 0x1c20) || (iVar2 * 0x15180 + 0x1c20 < iVar4))
      goto LAB_0040ddd4;
    }
    uVar3 = 0xe10;
  }
  return uVar3;
}



/* Address: 0040dddc */

int FUN_0040dddc(int param_1,int param_2)

{
  int iVar1;
  uint uVar2;
  
  uVar2 = (uint)*(ushort *)(&DAT_00410c58 + param_1 * 2);
  iVar1 = *(int *)(param_2 + 0x14);
  if (((iVar1 + 0x76c) % 4 == 0) && (1 < param_1)) {
    uVar2 = uVar2 + 1;
  }
  iVar1 = (int)((iVar1 + -0x46) * 0x16d + (iVar1 + -0x45 >> 2) + uVar2 + 4) % 7;
  if (iVar1 != 0) {
    iVar1 = 7 - iVar1;
  }
  return uVar2 + iVar1;
}



/* Address: 0040de48 */

int FUN_0040de48(int param_1,int param_2)

{
  int iVar1;
  uint uVar2;
  
  uVar2 = *(ushort *)(&DAT_00410c5a + param_1 * 2) - 1;
  iVar1 = *(int *)(param_2 + 0x14);
  if (((iVar1 + 0x76c) % 4 == 0) && (1 < param_1)) {
    uVar2 = (uint)*(ushort *)(&DAT_00410c5a + param_1 * 2);
  }
  return uVar2 - (int)((iVar1 + -0x46) * 0x16d + (iVar1 + -0x45 >> 2) + uVar2 + 4) % 7;
}



/* Address: 0040deb0 */

int FUN_0040deb0(int param_1)

{
  int iVar1;
  int iVar2;
  
  iVar1 = (uint)*(ushort *)(&DAT_00410c58 + *(int *)(param_1 + 0x10) * 2) + *(int *)(param_1 + 0xc);
  iVar2 = iVar1 + -1;
  if (((*(int *)(param_1 + 0x14) + 0x76c) % 4 == 0) && (1 < *(int *)(param_1 + 0x10))) {
    iVar2 = iVar1;
  }
  return iVar2;
}



/* Address: 0040def0 */

void FUN_0040def0(void)

{
  if (DAT_00410c74 == 0) {
    thunk_FUN_0040ede0();
    DAT_00410c74 = DAT_00410c74 + 1;
  }
  return;
}



/* Address: 0040df08 */

void __cdecl FUN_0040df08(undefined *param_1)

{
  (*(code *)param_1)(&stack0xfffffffc);
  return;
}



/* Address: 0040df44 */

void __cdecl FUN_0040df44(undefined4 param_1,PEXCEPTION_RECORD param_2)

{
  FUN_0040df58(param_2,(PVOID)0x0);
  return;
}



/* Address: 0040df58 */

void __cdecl FUN_0040df58(PEXCEPTION_RECORD param_1,PVOID param_2)

{
  int unaff_EBP;
  PVOID unaff_retaddr;
  
  RtlUnwind(*(PVOID *)(unaff_EBP + 8),unaff_retaddr,param_1,param_2);
  return;
}



/* Address: 0040df6c */

void __cdecl FUN_0040df6c(int param_1,int param_2)

{
  int *piVar1;
  undefined4 *unaff_FS_OFFSET;
  undefined4 uStack_2c;
  undefined1 *puStack_28;
  undefined4 uStack_24;
  undefined4 uStack_20;
  int local_8;
  
  local_8 = *(int *)(param_1 + 0xc);
  uStack_20 = 0xffffffff;
  uStack_24 = 0;
  puStack_28 = &LAB_0040df24;
  uStack_2c = *unaff_FS_OFFSET;
  *unaff_FS_OFFSET = &uStack_2c;
  while ((local_8 != -1 && (local_8 != param_2))) {
    piVar1 = (int *)(local_8 * 0xc + *(int *)(param_1 + 8));
    local_8 = *piVar1;
    if (piVar1[1] == 0) {
      FUN_0040df08((undefined *)piVar1[2]);
    }
  }
  *unaff_FS_OFFSET = uStack_2c;
  return;
}



/* Address: 0040dff0 */

void FUN_0040dff0(char *param_1)

{
  char cVar1;
  code *pcVar2;
  HANDLE hFile;
  uint uVar3;
  DWORD nNumberOfBytesToWrite;
  char *pcVar4;
  LPDWORD lpNumberOfBytesWritten;
  LPOVERLAPPED lpOverlapped;
  
  uVar3 = 0xffffffff;
  pcVar4 = param_1;
  do {
    if (uVar3 == 0) break;
    uVar3 = uVar3 - 1;
    cVar1 = *pcVar4;
    pcVar4 = pcVar4 + 1;
  } while (cVar1 != '\0');
  nNumberOfBytesToWrite = ~uVar3 - 1;
  lpNumberOfBytesWritten = (LPDWORD)&stack0xfffffff8;
  lpOverlapped = (LPOVERLAPPED)0x0;
  hFile = GetStdHandle(0xfffffff4);
  WriteFile(hFile,param_1,nNumberOfBytesToWrite,lpNumberOfBytesWritten,lpOverlapped);
  FUN_0040e400(1);
  pcVar2 = (code *)swi(3);
  (*pcVar2)();
  return;
}



/* Address: 0040e030 */

longlong __fastcall FUN_0040e030(int param_1,int param_2)

{
  uint in_EAX;
  uint unaff_EBX;
  
  if (param_1 != 0) {
    if (param_2 == 0) {
      return CONCAT44((int)((ulonglong)in_EAX * (ulonglong)unaff_EBX >> 0x20) + param_1 * in_EAX,
                      (int)((ulonglong)in_EAX * (ulonglong)unaff_EBX));
    }
    return CONCAT44((int)((ulonglong)in_EAX * (ulonglong)unaff_EBX >> 0x20) +
                    param_1 * in_EAX + param_2 * unaff_EBX,
                    (int)((ulonglong)in_EAX * (ulonglong)unaff_EBX));
  }
  if (param_2 == 0) {
    return (ulonglong)in_EAX * (ulonglong)unaff_EBX;
  }
  return CONCAT44((int)((ulonglong)in_EAX * (ulonglong)unaff_EBX >> 0x20) + param_2 * unaff_EBX,
                  (int)((ulonglong)in_EAX * (ulonglong)unaff_EBX));
}



/* Address: 0040e150 */

undefined4 __cdecl FUN_0040e150(undefined4 param_1,undefined4 param_2,uint param_3)

{
  FUN_0040d270(param_3);
  return param_1;
}



/* Address: 0040e170 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_0040e170(void)

{
  short sVar1;
  DWORD DVar2;
  
  _DAT_00410ce7 = GetCurrentThreadId();
  DAT_00410cb9 = GetEnvironmentStrings();
  DVar2 = GetVersion();
  _DAT_00410c86 = (undefined2)(DVar2 >> 0x10);
  _DAT_00410c8a = CONCAT11((char)DVar2,(char)(DVar2 >> 8));
  sVar1 = FUN_0040f090();
  if (sVar1 == -1) {
    FUN_0040dff0(s_Not_enough_memory_00410ca3);
  }
  FUN_0040f060();
  FUN_0040e1bc();
  return;
}



/* Address: 0040e1bc */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_0040e1bc(void)

{
  undefined **ppuVar1;
  undefined4 *puVar2;
  
  ppuVar1 = &PTR_FUN_00411154;
  while (&PTR_LAB_0041114c < ppuVar1) {
    ppuVar1 = ppuVar1 + -1;
    if ((code *)*ppuVar1 != (code *)0x0) {
      (*(code *)*ppuVar1)();
    }
  }
  _DAT_00410c7c = 0x41;
  puVar2 = (undefined4 *)&DAT_0041115c;
  while (&DAT_0041115c < puVar2) {
    puVar2 = puVar2 + -1;
    if ((code *)*puVar2 != (code *)0x0) {
      (*(code *)*puVar2)();
    }
  }
  puVar2 = (undefined4 *)&DAT_00410010;
  while (&DAT_00410000 < puVar2) {
    puVar2 = puVar2 + -1;
    if ((code *)*puVar2 != (code *)0x0) {
      (*(code *)*puVar2)();
    }
  }
  puVar2 = (undefined4 *)&DAT_00410030;
  while (&DAT_00410020 < puVar2) {
    puVar2 = puVar2 + -1;
    if ((code *)*puVar2 != (code *)0x0) {
      (*(code *)*puVar2)();
    }
  }
  return;
}



/* Address: 0040e237 */

void FUN_0040e237(void)

{
  code *pcVar1;
  undefined **ppuVar2;
  undefined *puVar3;
  undefined **ppuVar4;
  
  puVar3 = (undefined *)0x0;
  ppuVar2 = &PTR_FUN_00411154;
  while (ppuVar4 = ppuVar2, ppuVar4 < &DAT_0041115c) {
    pcVar1 = (code *)*ppuVar4;
    ppuVar2 = ppuVar4 + 1;
    if (pcVar1 != (code *)0x0) {
      *ppuVar4 = puVar3;
      puVar3 = (undefined *)(*pcVar1)();
    }
  }
  FUN_0040f078();
  FreeEnvironmentStringsA(DAT_00410cb9);
  return;
}



/* Address: 0040e274 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void __cdecl FUN_0040e274(UINT param_1)

{
  int iVar1;
  
  _DAT_00410d68 = 1;
  if ((DAT_00410d64 == 0) && (DAT_00410c78 != (int *)0x0)) {
    iVar1 = *DAT_00410c78;
    while (iVar1 != 0) {
      (*(code *)*DAT_00410c78)();
      DAT_00410c78 = DAT_00410c78 + -1;
      iVar1 = *DAT_00410c78;
    }
  }
  if (DAT_00416378 == (code *)0x0) {
    FUN_0040e237();
    if (DAT_00410d64 == 0) {
      if (DAT_0041637c != (code *)0x0) {
        (*DAT_0041637c)();
      }
      if (DAT_00410d60 == 0) {
        FUN_0040e400(param_1);
      }
    }
  }
  else {
    (*DAT_00416378)(param_1);
  }
  DAT_00410d64 = 0;
  DAT_00410d60 = 0;
  return;
}



/* Address: 0040e30c */

LONG __cdecl FUN_0040e30c(_EXCEPTION_POINTERS *param_1)

{
  DWORD DVar1;
  code *pcVar2;
  code *pcVar3;
  LONG LVar4;
  undefined4 uVar5;
  code *pcVar6;
  int iVar7;
  
  pcVar3 = DAT_00410f44;
  pcVar2 = DAT_00410f40;
  pcVar6 = DAT_00410f3c;
  uVar5 = 0;
  iVar7 = 0;
  DVar1 = param_1->ExceptionRecord->ExceptionCode;
  if (DVar1 == 0xc000008d) {
    uVar5 = 0x82;
  }
  else if (DVar1 == 0xc000008e) {
    uVar5 = 0x83;
  }
  else if (DVar1 == 0xc000008f) {
    uVar5 = 0x86;
  }
  else if (DVar1 == 0xc0000090) {
    uVar5 = 0x81;
  }
  else if (DVar1 == 0xc0000091) {
    uVar5 = 0x84;
  }
  else if (DVar1 == 0xc0000092) {
    uVar5 = 0x8a;
  }
  else {
    if (DVar1 != 0xc0000093) {
      if (DVar1 == 0xc0000005) {
        iVar7 = 0xb;
        DAT_00410f44 = (code *)0x0;
        pcVar6 = pcVar3;
      }
      else if ((DVar1 == 0xc000001d) || (DVar1 == 0xc0000096)) {
        iVar7 = 4;
        DAT_00410f40 = (code *)0x0;
        pcVar6 = pcVar2;
      }
      else {
        pcVar6 = (code *)0x0;
      }
      goto LAB_0040e3d1;
    }
    uVar5 = 0x85;
  }
  iVar7 = 8;
  DAT_00410f3c = (code *)0x0;
LAB_0040e3d1:
  if (pcVar6 != (code *)0x0) {
    if (iVar7 == 8) {
      (*pcVar6)(8,uVar5);
    }
    else {
      (*pcVar6)(iVar7);
    }
    return -1;
  }
  LVar4 = UnhandledExceptionFilter(param_1);
  return LVar4;
}



/* Address: 0040e400 */

void FUN_0040e400(UINT param_1)

{
                    /* WARNING: Subroutine does not return */
  ExitProcess(param_1);
}



/* Address: 0040e40c */

undefined4 __cdecl FUN_0040e40c(int param_1,int param_2,undefined4 param_3)

{
  int iVar1;
  undefined *puVar2;
  int iVar3;
  int iVar4;
  int *piVar5;
  int local_c;
  undefined4 local_8;
  int local_4;
  
  if ((*(byte *)(param_1 + 4) & 6) == 0) {
    local_c = param_1;
    local_8 = param_3;
    *(int **)(param_2 + -4) = &local_c;
    if (*(int *)(param_2 + 0xc) != -1) {
      local_4 = param_2 + 0x10;
      iVar4 = *(int *)(param_2 + 0xc);
      do {
        piVar5 = (int *)(iVar4 * 0xc + *(int *)(param_2 + 8));
        iVar1 = *piVar5;
        puVar2 = (undefined *)piVar5[2];
        if ((undefined *)piVar5[1] != (undefined *)0x0) {
          iVar3 = FUN_0040df08((undefined *)piVar5[1]);
          if (iVar3 == -1) {
            return 0;
          }
          if ((iVar3 != 0) && (iVar3 == 1)) {
            FUN_0040df44(param_2,(PEXCEPTION_RECORD)0x0);
            FUN_0040df6c(param_2,iVar4);
            FUN_0040df08(puVar2);
            return 1;
          }
        }
        iVar4 = iVar1;
      } while (iVar1 != -1);
    }
  }
  else {
    FUN_0040df6c(param_2,-1);
  }
  return 1;
}



/* Address: 0040e500 */

void FUN_0040e500(void)

{
  int iVar1;
  undefined1 *puVar2;
  
  if (DAT_00410d6c == 0) {
    puVar2 = &DAT_0041115c;
    for (iVar1 = 0x5624; iVar1 != 0; iVar1 = iVar1 + -1) {
      *puVar2 = 0;
      puVar2 = puVar2 + 1;
    }
    DAT_00410d6c = 1;
  }
  return;
}



/* Address: 0040e534 */

void FUN_0040e534(void)

{
  DWORD DVar1;
  char *pcVar2;
  uint uVar3;
  
  FUN_0040d2a4(&DAT_00416380,0xff,0x8c);
  FUN_0040d2a4(&DAT_0041640c,0,0x118);
  FUN_0040d2a4(&DAT_00416524,0,0x118);
  FUN_0040d2a4(&DAT_0041663c,0,0x118);
  uVar3 = 0x10;
  pcVar2 = &DAT_00416771;
  DVar1 = GetTickCount();
  FUN_0040c720(DVar1,pcVar2,uVar3);
  return;
}



/* Address: 0040e590 */

void __cdecl FUN_0040e590(uint param_1)

{
  int *piVar1;
  int iVar2;
  char *pcVar3;
  HANDLE pvVar4;
  uint local_30 [7];
  char local_14 [20];
  
  FUN_0040c5d8(local_30,(uint *)&DAT_00416771);
  pcVar3 = FUN_0040c720(param_1,local_14,0x10);
  FUN_0040dc60((char *)local_30,pcVar3);
  piVar1 = (int *)(&DAT_0041663c + param_1 * 4);
  iVar2 = *piVar1;
  while (iVar2 == 0) {
    pvVar4 = CreateSemaphoreA((LPSECURITY_ATTRIBUTES)0x0,0,1,(LPCSTR)local_30);
    if (pvVar4 != (HANDLE)0x0) {
      *piVar1 = (int)pvVar4;
    }
    iVar2 = *piVar1;
  }
  WaitForSingleObject(*(HANDLE *)(&DAT_0041663c + param_1 * 4),0xffffffff);
  return;
}



/* Address: 0040e60c */

void __cdecl FUN_0040e60c(uint param_1)

{
  int *piVar1;
  int iVar2;
  char *pcVar3;
  HANDLE pvVar4;
  uint local_30 [7];
  char local_14 [20];
  
  FUN_0040c5d8(local_30,(uint *)&DAT_00416771);
  pcVar3 = FUN_0040c720(param_1,local_14,0x10);
  FUN_0040dc60((char *)local_30,pcVar3);
  piVar1 = (int *)(&DAT_0041663c + param_1 * 4);
  iVar2 = *piVar1;
  while (iVar2 == 0) {
    pvVar4 = CreateSemaphoreA((LPSECURITY_ATTRIBUTES)0x0,0,1,(LPCSTR)local_30);
    if (pvVar4 != (HANDLE)0x0) {
      *piVar1 = (int)pvVar4;
    }
    iVar2 = *piVar1;
  }
  ReleaseSemaphore(*(HANDLE *)(&DAT_0041663c + param_1 * 4),1,(LPLONG)0x0);
  return;
}



/* Address: 0040e688 */

int FUN_0040e688(void)

{
  LPSTR lpBuffer;
  UINT uSize;
  char *pcVar1;
  int iVar2;
  int extraout_EDX;
  uint *lpBuffer_00;
  int iVar3;
  CHAR *pCVar4;
  CHAR local_88 [128];
  CHAR *pCStack_8;
  DWORD DStack_4;
  
  lpBuffer_00 = (uint *)0x0;
  pCVar4 = local_88;
  for (iVar2 = 0x80; iVar2 != 0; iVar2 = iVar2 + -1) {
    *pCVar4 = '\0';
    pCVar4 = pCVar4 + 1;
  }
  DStack_4 = GetVersion();
  if (((DStack_4 & 0x80000000) == 0) || (3 < (byte)DStack_4)) {
    iVar3 = 0x10f;
LAB_0040e7a4:
    if (lpBuffer_00 != (uint *)0x0) {
      FUN_0040f4a4(lpBuffer_00);
    }
  }
  else {
    lpBuffer = (LPSTR)FUN_0040f0e4(1);
    if (lpBuffer != (LPSTR)0x0) {
      uSize = GetSystemDirectoryA(lpBuffer,0);
      pcVar1 = FUN_0040c63c((uint *)s__win32s_ini_00410d70);
      lpBuffer_00 = FUN_0040f28c((undefined4 *)lpBuffer,(uint)(pcVar1 + uSize + 1));
      if (lpBuffer_00 != (uint *)0x0) {
        GetSystemDirectoryA((LPSTR)lpBuffer_00,uSize);
        pcVar1 = FUN_0040c63c(lpBuffer_00);
        if ((pcVar1 + -1)[(int)lpBuffer_00] != '\\') {
          FUN_0040dc60((char *)lpBuffer_00,&DAT_00410d7c);
        }
        FUN_0040dc60((char *)lpBuffer_00,s_win32s_ini_00410d93);
        GetPrivateProfileStringA
                  (s_Win32s_00410d7e,s_Version_00410d85,s_1_0_0_00410d8d,local_88,0x80,
                   (LPCSTR)lpBuffer_00);
        pCStack_8 = local_88;
        iVar2 = FUN_0040f318(pCStack_8,(int)&pCStack_8,pCStack_8,&pCStack_8,10);
        pCStack_8 = pCStack_8 + 1;
        iVar3 = FUN_0040f318(&pCStack_8,extraout_EDX,pCStack_8,&pCStack_8,10);
        iVar3 = iVar3 + iVar2 * 0x100;
        goto LAB_0040e7a4;
      }
    }
    iVar3 = 0;
  }
  return iVar3;
}



/* Address: 0040e7c0 */

undefined4 FUN_0040e7c0(void)

{
  int iVar1;
  
  iVar1 = FUN_0040e688();
  if (iVar1 < 0x10f) {
    MessageBoxA((HWND)0x0,s_Wrong_Win32s_version__or_damaged_00410d9f,s_Startup_Error_00410e19,0x10)
    ;
    FUN_0040dff0(&DAT_00410d9e);
    return 0;
  }
  return 1;
}



/* Address: 0040e800 */

undefined4 * FUN_0040e800(void)

{
  undefined4 *puVar1;
  DWORD DVar2;
  int iVar3;
  int *piVar4;
  
  puVar1 = FUN_0040f4f8(0x1004,1);
  if (puVar1 != (undefined4 *)0x0) {
    DAT_00410e30 = puVar1;
    puVar1[1] = &DAT_00410ccf;
    return puVar1;
  }
  FUN_0040dff0(s_Thread_error_00410e34);
  DVar2 = GetCurrentThreadId();
  iVar3 = 0x400;
  piVar4 = DAT_00410e30;
  while ((puVar1 = (undefined4 *)*piVar4, puVar1 == (undefined4 *)0x0 || (DVar2 != puVar1[6]))) {
    piVar4 = piVar4 + 1;
    iVar3 = iVar3 + -1;
    if (iVar3 == 0) {
      return (undefined4 *)0x0;
    }
  }
  return puVar1;
}



/* Address: 0040e82e */

undefined8 __fastcall FUN_0040e82e(undefined4 param_1,undefined4 param_2)

{
  DWORD DVar1;
  int iVar2;
  int iVar3;
  int *piVar4;
  
  DVar1 = GetCurrentThreadId();
  iVar3 = 0x400;
  piVar4 = DAT_00410e30;
  do {
    iVar2 = *piVar4;
    if ((iVar2 != 0) && (DVar1 == *(DWORD *)(iVar2 + 0x18))) goto LAB_0040e859;
    piVar4 = piVar4 + 1;
    iVar3 = iVar3 + -1;
  } while (iVar3 != 0);
  iVar2 = 0;
LAB_0040e859:
  return CONCAT44(param_2,iVar2);
}



/* Address: 0040e8e0 */

void FUN_0040e8e0(void)

{
  if (DAT_00416754 != (code *)0x0) {
    (*DAT_00416754)();
  }
  return;
}



/* Address: 0040e8f0 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_0040e8f0(void)

{
  char cVar1;
  bool bVar2;
  uint *puVar3;
  uint uVar4;
  char *pcVar5;
  uint uVar6;
  uint uVar7;
  uint *puVar8;
  uint *puVar9;
  uint *puVar10;
  int local_c;
  
  DAT_00410e28 = (uint *)FUN_0040eb58((char *)DAT_00410e28);
  puVar3 = FUN_0040ea9c(DAT_00410e28);
  if (puVar3 != (uint *)0x0) {
    local_c = 0;
    puVar9 = puVar3;
    puVar10 = puVar3;
LAB_0040e93c:
    for (; cVar1 = (char)*puVar10, cVar1 != '\0'; puVar10 = (uint *)((int)puVar10 + 1)) {
      if ((cVar1 != ' ') && (cVar1 != '\t')) {
        local_c = local_c + 1;
        bVar2 = false;
        uVar7 = 0;
        puVar8 = puVar9;
        do {
          while( true ) {
            cVar1 = (char)*puVar10;
            puVar10 = (uint *)((int)puVar10 + 1);
            if (cVar1 != '\"') break;
            uVar6 = (int)uVar7 >> 0x1f;
            uVar4 = uVar7 ^ uVar6;
            for (uVar7 = (int)uVar7 / 2; 0 < (int)uVar7; uVar7 = uVar7 - 1) {
              puVar8 = (uint *)((int)puVar8 + -1);
              *(char *)puVar8 = '\0';
            }
            if (uVar6 + (uVar4 & 1) == 0) {
              bVar2 = (bool)(bVar2 ^ 1);
            }
            else {
              *(char *)((int)puVar8 + -1) = '\"';
            }
          }
          if ((cVar1 == ' ') || (cVar1 == '\t')) {
            if (!bVar2) goto LAB_0040e9b1;
          }
          else if (cVar1 == '\0') goto LAB_0040e9b1;
          if (cVar1 == '\\') {
            uVar7 = uVar7 + 1;
          }
          else {
            uVar7 = 0;
          }
          *(char *)puVar8 = cVar1;
          puVar8 = (uint *)((int)puVar8 + 1);
        } while( true );
      }
    }
    goto LAB_0040e9c2;
  }
LAB_0040e91b:
  FUN_0040dff0(s_Out_of_memory_00410e48);
  return;
LAB_0040e9b1:
  puVar9 = (uint *)((int)puVar8 + 1);
  *(char *)puVar8 = '\0';
  if (cVar1 == '\0') goto LAB_0040e9c2;
  goto LAB_0040e93c;
LAB_0040e9c2:
  DAT_00410cbd = local_c;
  DAT_00410cc1 = FUN_0040f0e4(local_c * 4 + 4);
  if (DAT_00410cc1 != (undefined4 *)0x0) {
    local_c = 0;
    if (0 < DAT_00410cbd) {
      local_c = 0;
      do {
        *(uint **)(local_c + (int)DAT_00410cc1) = puVar3;
        pcVar5 = FUN_0040c63c(puVar3);
        puVar3 = (uint *)((int)puVar3 + (int)(pcVar5 + 1));
        local_c = local_c + 4;
      } while (local_c < DAT_00410cbd * 4);
      local_c = local_c / 4;
    }
    DAT_00410cc1[local_c] = 0;
    _DAT_00410c82 = *DAT_00410cc1;
    return;
  }
  goto LAB_0040e91b;
}



/* Address: 0040ea9c */

uint * __cdecl FUN_0040ea9c(uint *param_1)

{
  char *pcVar1;
  uint *puVar2;
  DWORD DVar3;
  uint local_104 [65];
  
  pcVar1 = FUN_0040c63c(param_1);
  puVar2 = FUN_0040f0e4((uint)(pcVar1 + 0x108));
  *(undefined1 *)puVar2 = 0;
  if (puVar2 != (uint *)0x0) {
    DVar3 = GetModuleFileNameA((HMODULE)0x0,(LPSTR)local_104,0x104);
    if (DVar3 != 0) {
      pcVar1 = FUN_0040c69c((char *)local_104,'\"');
      if (pcVar1 == (char *)0x0) {
        FUN_0040dc60((char *)puVar2,&DAT_00410e56);
        FUN_0040dc60((char *)puVar2,(char *)local_104);
        FUN_0040dc60((char *)puVar2,&DAT_00410e56);
      }
      else {
        FUN_0040c5d8(puVar2,local_104);
      }
      FUN_0040dc60((char *)puVar2,&DAT_00410e58);
      FUN_0040dc60((char *)puVar2,(char *)param_1);
      return puVar2;
    }
  }
  return (uint *)0x0;
}



/* Address: 0040eb58 */

char * __cdecl FUN_0040eb58(char *param_1)

{
  char cVar1;
  bool bVar2;
  bool bVar3;
  
  bVar2 = false;
  bVar3 = false;
  do {
    cVar1 = *param_1;
    if (cVar1 == '\"') {
      bVar2 = (bool)(bVar2 ^ 1);
      param_1 = param_1 + 1;
    }
    else if (cVar1 == '\0') {
      bVar3 = true;
    }
    else {
      if (((cVar1 == ' ') || (cVar1 == '\t')) && (!bVar2)) {
        bVar3 = true;
      }
      param_1 = param_1 + 1;
    }
  } while (!bVar3);
  cVar1 = *param_1;
  while ((cVar1 < '!' && (cVar1 != '\0'))) {
    param_1 = param_1 + 1;
    cVar1 = *param_1;
  }
  return param_1;
}



/* Address: 0040ebb4 */

bool __cdecl FUN_0040ebb4(int param_1)

{
  DWORD DVar1;
  
  DVar1 = GetFileType(*(HANDLE *)(&DAT_00410b64 + param_1 * 4));
  return DVar1 == 2;
}



/* Address: 0040ebd4 */

void __fastcall FUN_0040ebd4(undefined4 param_1,undefined4 param_2,undefined4 param_3)

{
  int iVar1;
  undefined8 uVar2;
  
  uVar2 = FUN_0040e82e(param_1,param_2);
  iVar1 = (int)uVar2;
  if (iVar1 == 0) {
    return;
  }
  *(undefined4 *)(iVar1 + 0x10) = param_3;
  *(undefined4 *)(iVar1 + 0x8c) = 0;
  return;
}



/* Address: 0040ebfc */

undefined4 __fastcall FUN_0040ebfc(undefined4 param_1,undefined4 param_2)

{
  int iVar1;
  DWORD DVar2;
  byte *pbVar3;
  undefined8 uVar4;
  
  uVar4 = FUN_0040e82e(param_1,param_2);
  iVar1 = (int)uVar4;
  if (iVar1 == 0) {
    return 0x16;
  }
  DVar2 = GetLastError();
  *(DWORD *)(iVar1 + 0x8c) = DVar2;
  pbVar3 = &DAT_00410e60;
  do {
    if ((uint)*pbVar3 == *(uint *)(iVar1 + 0x8c)) {
      *(uint *)(iVar1 + 0x10) = (uint)pbVar3[1];
      return *(undefined4 *)(iVar1 + 0x8c);
    }
    pbVar3 = pbVar3 + 2;
  } while (pbVar3 < &DAT_00410eb8);
  if ((*(int *)(iVar1 + 0x8c) < 0x13) || (0x24 < *(int *)(iVar1 + 0x8c))) {
    if ((*(int *)(iVar1 + 0x8c) < 0xbc) || (0xca < *(int *)(iVar1 + 0x8c))) {
      *(undefined4 *)(iVar1 + 0x10) = 0x16;
    }
    else {
      *(undefined4 *)(iVar1 + 0x10) = 8;
    }
  }
  else {
    *(undefined4 *)(iVar1 + 0x10) = 0xd;
  }
  return *(undefined4 *)(iVar1 + 0x8c);
}



/* 0040ec98: thunk_FUN_0040ede0; Thunk to FUN_0040ede0 */

/* Address: 0040eca0 */

char * FUN_0040eca0(char *param_1)

{
  char cVar1;
  byte bVar2;
  
  cVar1 = *param_1;
  if (cVar1 != '\0') {
    if (cVar1 == '+') goto LAB_0040ecc2;
    if (cVar1 == '-') goto LAB_0040ecc2;
    if (cVar1 == ':') goto LAB_0040ecc2;
    bVar2 = (&DAT_004109f9)[cVar1];
    while ((bVar2 & 4) != 0) {
LAB_0040ecc2:
      do {
        param_1 = param_1 + 1;
        cVar1 = *param_1;
        if (cVar1 == '\0') goto LAB_0040ece1;
      } while (((cVar1 == '+') || (cVar1 == '-')) || (cVar1 == ':'));
      bVar2 = (&DAT_004109f9)[cVar1];
    }
  }
LAB_0040ece1:
  cVar1 = *param_1;
  if (((cVar1 != '\0') && (cVar1 != ';')) && ((cVar1 != ',' && ((cVar1 != '+' && (cVar1 != '-'))))))
  {
    bVar2 = (&DAT_004109f9)[cVar1];
    while ((bVar2 & 7) == 0) {
      param_1 = param_1 + 1;
      cVar1 = *param_1;
      if (cVar1 == '\0') {
        return param_1;
      }
      if (cVar1 == ';') {
        return param_1;
      }
      if (cVar1 == ',') {
        return param_1;
      }
      if (cVar1 == '+') {
        return param_1;
      }
      if (cVar1 == '-') {
        return param_1;
      }
      bVar2 = (&DAT_004109f9)[cVar1];
    }
  }
  return param_1;
}



/* Address: 0040ed2c */

int FUN_0040ed2c(byte *param_1)

{
  byte bVar1;
  byte *pbVar2;
  int iVar3;
  int iVar4;
  int iVar5;
  
  iVar4 = 0;
  if ((param_1 != (byte *)0x0) && (*param_1 != 0)) {
    bVar1 = (&DAT_004109f9)[(char)*param_1];
    while ((bVar1 & 3) != 0) {
      param_1 = param_1 + 1;
      bVar1 = (&DAT_004109f9)[(char)*param_1];
    }
    if (*param_1 != 0) {
      iVar5 = 1;
      iVar4 = thunk_FUN_0040c58c(param_1);
      iVar4 = iVar4 * 0xe10;
      if (iVar4 < 0) {
        iVar5 = -1;
      }
      pbVar2 = (byte *)FUN_0040eca0((char *)param_1);
      if (((*pbVar2 != 0) && (*pbVar2 != 0x3b)) && (*pbVar2 != 0x2c)) {
        iVar3 = thunk_FUN_0040c58c(pbVar2);
        iVar4 = iVar4 + iVar3 * 0x3c * iVar5;
        pbVar2 = (byte *)FUN_0040eca0((char *)pbVar2);
        if (((*pbVar2 != 0) && (*pbVar2 != 0x3b)) && (*pbVar2 != 0x2c)) {
          iVar3 = thunk_FUN_0040c58c(pbVar2);
          iVar4 = iVar4 + iVar3 * iVar5;
        }
      }
      FUN_0040eca0((char *)pbVar2);
    }
  }
  return iVar4;
}



/* Address: 0040ede0 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_0040ede0(void)

{
  byte bVar1;
  byte *pbVar2;
  int iVar3;
  uint uVar4;
  DWORD DVar5;
  undefined4 extraout_ECX;
  byte *pbVar6;
  byte *pbVar7;
  uint uVar8;
  _TIME_ZONE_INFORMATION local_c8;
  int local_18;
  int local_14;
  int local_10;
  uint local_c;
  undefined4 local_8;
  undefined4 local_4;
  
  pbVar2 = (byte *)FUN_0040f548((uint *)&DAT_00410f2f);
  if ((pbVar2 != (byte *)0x0) && (*pbVar2 != 0)) {
    iVar3 = FUN_0040c708((int)(char)*pbVar2);
    if (0x40 < iVar3) {
      uVar4 = FUN_0040c708((int)(char)*pbVar2);
      if ((int)uVar4 < 0x5b) {
        uVar8 = 0;
        bVar1 = *pbVar2;
        local_18 = CONCAT31((int3)((uint)extraout_ECX >> 8),bVar1);
        iVar3 = local_18;
        pbVar7 = pbVar2;
        if ((bVar1 != 0) && (iVar3 = (int)(char)bVar1, ((&DAT_004109f9)[(char)bVar1] & 4) == 0)) {
          iVar3 = local_18;
          if ((bVar1 != 0x2d) && (pbVar6 = pbVar2, bVar1 != 0x2b)) {
            while( true ) {
              uVar4 = uVar8;
              pbVar7 = pbVar6 + 1;
              uVar8 = uVar4 + 1;
              (&DAT_00410f08)[uVar4] = *pbVar6;
              bVar1 = *pbVar7;
              local_14 = CONCAT31((int3)((uint)pbVar6 >> 8),bVar1);
              iVar3 = local_14;
              if ((bVar1 == 0) ||
                 (iVar3 = (int)(char)bVar1, ((&DAT_004109f9)[(char)bVar1] & 4) != 0)) break;
              iVar3 = local_14;
              if ((bVar1 == 0x2d) || ((bVar1 == 0x2b || (pbVar6 = pbVar7, 8 < uVar8)))) break;
            }
          }
        }
        (&DAT_00410f08)[uVar8] = 0;
        bVar1 = *pbVar7;
        local_10 = CONCAT31((int3)((uint)iVar3 >> 8),bVar1);
        iVar3 = local_10;
        if (bVar1 != 0) {
          iVar3 = (int)(char)bVar1;
          if (((&DAT_004109f9)[(char)bVar1] & 4) == 0) {
            iVar3 = local_10;
            if ((((bVar1 != 0x3a) && (bVar1 != 0x2d)) && (bVar1 != 0x2b)) && (bVar1 != 0x3b))
            goto LAB_0040ef0a;
          }
          do {
            do {
              pbVar7 = pbVar7 + 1;
              bVar1 = *pbVar7;
              local_c = CONCAT31((int3)(uVar4 >> 8),bVar1);
              if (bVar1 == 0) goto LAB_0040ef0a;
              uVar4 = (int)(char)bVar1;
            } while (((&DAT_004109f9)[(char)bVar1] & 4) != 0);
            uVar4 = local_c;
          } while (((bVar1 == 0x3a) || (bVar1 == 0x2d)) || ((bVar1 == 0x2b || (bVar1 == 0x3b))));
        }
LAB_0040ef0a:
        DAT_00410f04 = (uint)(*pbVar7 != 0);
        if (*pbVar7 != 0) {
          uVar4 = 0;
          bVar1 = *pbVar7;
          local_8 = CONCAT31((int3)((uint)iVar3 >> 8),bVar1);
          if (((bVar1 != 0) && (((&DAT_004109f9)[(char)bVar1] & 4) == 0)) &&
             ((bVar1 != 0x2d && (uVar8 = uVar4, bVar1 != 0x2b)))) {
            while( true ) {
              uVar4 = uVar8 + 1;
              (&DAT_00410f14)[uVar8] = *pbVar7;
              bVar1 = pbVar7[1];
              local_4 = CONCAT31((int3)((uint)pbVar7 >> 8),bVar1);
              if (((bVar1 == 0) || (((&DAT_004109f9)[(char)bVar1] & 4) != 0)) || (bVar1 == 0x2d))
              break;
              if ((bVar1 == 0x2b) || (pbVar7 = pbVar7 + 1, uVar8 = uVar4, 8 < uVar4)) break;
            }
          }
          (&DAT_00410f14)[uVar4] = 0;
        }
        else {
          DAT_00410f14 = 0;
        }
        DAT_00410efc = FUN_0040ed2c(pbVar2);
        goto LAB_0040f03f;
      }
    }
  }
  DVar5 = GetTimeZoneInformation(&local_c8);
  if ((DVar5 == 0xffffffff) || (DVar5 == 0)) {
    FUN_0040c5d8((uint *)&DAT_00410f08,(uint *)&DAT_00410f36);
    FUN_0040c5d8((uint *)&DAT_00410f14,(uint *)&DAT_00410f32);
    DAT_00410f04 = 1;
    DAT_00410efc = 0x7080;
  }
  else {
    DAT_00410efc = local_c8.Bias * 0x3c;
    if (local_c8.StandardDate.wMonth != 0) {
      DAT_00410efc = DAT_00410efc + local_c8.StandardBias * 0x3c;
    }
    if ((local_c8.DaylightDate.wMonth == 0) || (local_c8.DaylightBias == 0)) {
      DAT_00410f04 = 0;
    }
    else {
      DAT_00410f04 = 1;
    }
    DAT_00410f08 = 0;
    DAT_00410f14 = 0;
  }
LAB_0040f03f:
  if (DAT_00410f04 != 0) {
    _DAT_00410f00 = DAT_00410efc + -0xe10;
  }
  return;
}



/* Address: 0040f060 */

void FUN_0040f060(void)

{
  if (DAT_00416758 != (code *)0x0) {
    (*DAT_00416758)(1);
  }
  return;
}



/* Address: 0040f078 */

void FUN_0040f078(void)

{
  if (DAT_00416758 != (code *)0x0) {
    (*DAT_00416758)(0);
  }
  return;
}



/* Address: 0040f090 */

void FUN_0040f090(void)

{
  if (PTR_FUN_00411148 != (undefined *)0x0) {
    (*(code *)PTR_FUN_00411148)();
  }
  return;
}



/* Address: 0040f0a0 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_0040f0a0(void)

{
  uint uVar1;
  undefined *puVar2;
  undefined4 *puVar3;
  
  puVar3 = (undefined4 *)(DAT_00410f48 + _DAT_00410f4c);
  puVar2 = &DAT_00410f50;
  for (uVar1 = 0x10000 - _DAT_00410f4c; uVar1 != 0; uVar1 = (int)uVar1 >> 1) {
    if ((uVar1 & 8) != 0) {
      *puVar3 = puVar2;
      FUN_0040f4e4(puVar3 + 1);
      puVar3 = (undefined4 *)((int)puVar3 + *(int *)(puVar2 + 4));
    }
    puVar2 = puVar2 + 8;
  }
  return;
}



/* Address: 0040f0e4 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 * __cdecl FUN_0040f0e4(uint param_1)

{
  int *piVar1;
  int iVar2;
  int iVar3;
  undefined4 *local_c;
  
  LOCK();
  _DAT_00416382 = _DAT_00416382 + 1;
  UNLOCK();
  if (_DAT_00416382 != 0) {
    FUN_0040e590(1);
  }
  param_1 = param_1 + 4;
  if (param_1 < 9) {
    param_1 = 8;
  }
  if ((param_1 & 0xffffff00) == 0) {
    iVar3 = (int)*(char *)(param_1 + 0x411037);
  }
  else if ((param_1 & 0xffff0000) == 0) {
    iVar3 = (char)(&DAT_00411038)[param_1 - 1 >> 8] + 8;
  }
  else if ((param_1 & 0xff000000) == 0) {
    iVar3 = (char)(&DAT_00411038)[param_1 - 1 >> 0x10] + 0x10;
  }
  else {
    iVar3 = (char)(&DAT_00411038)[param_1 - 1 >> 0x18] + 0x18;
  }
  piVar1 = (int *)(&DAT_00410f50 + iVar3 * 8);
  iVar2 = *piVar1;
  if (iVar2 == 0) {
    if (param_1 < 0x10001) {
      if (0xffff < (uint)((&DAT_00410f54)[iVar3 * 2] + _DAT_00410f4c)) {
        FUN_0040f0a0();
        DAT_00410f48 = FUN_0040f624(0x10000);
        if (DAT_00410f48 == (LPVOID)0xffffffff) {
          DAT_00410f48 = (LPVOID)0x0;
          _DAT_00410f4c = 0x10000;
          local_c = (undefined4 *)0x0;
          LOCK();
          _DAT_00416382 = _DAT_00416382 + -1;
          UNLOCK();
          if (_DAT_00416382 < 0) {
            return local_c;
          }
          goto LAB_0040f27c;
        }
        _DAT_00410f4c = 0;
      }
      local_c = (undefined4 *)((int)DAT_00410f48 + _DAT_00410f4c);
      _DAT_00410f4c = _DAT_00410f4c + (&DAT_00410f54)[iVar3 * 2];
      *local_c = piVar1;
    }
    else {
      local_c = (undefined4 *)FUN_0040f624((&DAT_00410f54)[iVar3 * 2]);
      if (local_c == (undefined4 *)0xffffffff) {
        local_c = (undefined4 *)0x0;
        LOCK();
        _DAT_00416382 = _DAT_00416382 + -1;
        UNLOCK();
        if (_DAT_00416382 < 0) {
          return local_c;
        }
        goto LAB_0040f27c;
      }
      *local_c = piVar1;
    }
    local_c = local_c + 1;
    LOCK();
    _DAT_00416382 = _DAT_00416382 + -1;
    UNLOCK();
    if (_DAT_00416382 < 0) {
      return local_c;
    }
  }
  else {
    *piVar1 = *(int *)(iVar2 + 4);
    local_c = (undefined4 *)(iVar2 + 4);
    LOCK();
    _DAT_00416382 = _DAT_00416382 + -1;
    UNLOCK();
    if (_DAT_00416382 < 0) {
      return local_c;
    }
  }
LAB_0040f27c:
  FUN_0040e60c(1);
  return local_c;
}



/* Address: 0040f28c */

undefined4 * __cdecl FUN_0040f28c(undefined4 *param_1,uint param_2)

{
  undefined4 *puVar1;
  uint uVar2;
  
  if (param_1 == (undefined4 *)0x0) {
    puVar1 = FUN_0040f0e4(param_2);
    return puVar1;
  }
  if (param_2 == 0) {
    FUN_0040f4a4(param_1);
    return (undefined4 *)0x0;
  }
  uVar2 = FUN_0040f6cc((int)param_1);
  if (param_2 <= uVar2) {
    return param_1;
  }
  puVar1 = FUN_0040f0e4(param_2);
  if (puVar1 == (undefined4 *)0x0) {
    return (undefined4 *)0x0;
  }
  FUN_0040c2a0(puVar1,param_1,uVar2);
  FUN_0040f4a4(param_1);
  return puVar1;
}



/* Address: 0040f318 */

int __fastcall
FUN_0040f318(undefined4 param_1,int param_2,char *param_3,undefined4 *param_4,int param_5)

{
  byte bVar1;
  char cVar2;
  bool bVar3;
  bool bVar4;
  undefined4 *puVar5;
  char *pcVar6;
  char *pcVar7;
  int iVar8;
  int iVar9;
  
  iVar9 = 0;
  iVar8 = 0;
  bVar1 = (&DAT_004109f9)[*param_3];
  pcVar6 = param_3;
  while ((bVar1 & 8) != 0) {
    pcVar6 = pcVar6 + 1;
    bVar1 = (&DAT_004109f9)[*pcVar6];
  }
  if (*pcVar6 == '-') {
    bVar3 = false;
    pcVar6 = pcVar6 + 1;
  }
  else {
    if (*pcVar6 == '+') {
      pcVar6 = pcVar6 + 1;
    }
    bVar3 = true;
  }
  pcVar7 = pcVar6;
  if (param_5 == 0) {
    param_5 = 10;
    if (*pcVar6 == '0') {
      param_5 = 8;
      iVar8 = 1;
      cVar2 = pcVar6[1];
      if ((cVar2 == 'x') || (cVar2 == 'X')) {
        param_5 = 0x10;
        iVar8 = 0;
        pcVar7 = pcVar6 + 2;
      }
      else if ((cVar2 == 'b') || (pcVar7 = pcVar6 + 1, cVar2 == 'B')) {
        param_5 = 2;
        iVar8 = 0;
        pcVar7 = pcVar6 + 2;
      }
    }
  }
  else {
    if (param_5 == 0x10) {
      if ((*pcVar6 != '0') || ((pcVar6[1] != 'x' && (pcVar6[1] != 'X')))) goto LAB_0040f3e6;
    }
    else if ((param_5 != 2) || ((*pcVar6 != '0' || ((pcVar6[1] != 'b' && (pcVar6[1] != 'B'))))))
    goto LAB_0040f3e6;
    pcVar7 = pcVar6 + 2;
  }
LAB_0040f3e6:
  bVar4 = false;
  do {
    puVar5 = (undefined4 *)(int)*pcVar7;
    if ((*(byte *)((int)puVar5 + 0x4109f9) & 4) == 0) {
      if ((*(byte *)((int)puVar5 + 0x4109f9) & 3) == 0) {
LAB_0040f49d:
        if (iVar8 == 0) {
          pcVar7 = param_3;
        }
        if (param_4 != (undefined4 *)0x0) {
          *param_4 = pcVar7;
          puVar5 = param_4;
        }
        if (((!bVar3) || (iVar9 != -0x80000000)) && (!bVar4)) {
          if (bVar3) {
            iVar9 = -iVar9;
          }
          return iVar9;
        }
        FUN_0040ebd4(puVar5,param_2,0x22);
        return (-(uint)!bVar3 & 1) + 0x7fffffff;
      }
      puVar5 = (undefined4 *)(((int)*pcVar7 & 0xffffffdfU) - 0x37);
    }
    else {
      puVar5 = puVar5 + -0xc;
    }
    if (param_5 <= (int)puVar5) goto LAB_0040f49d;
    if (iVar9 < (int)(puVar5 + -0x20000000) / param_5) {
      bVar4 = true;
    }
    param_2 = param_5 * iVar9;
    iVar9 = param_2 - (int)puVar5;
    pcVar7 = pcVar7 + 1;
    iVar8 = iVar8 + 1;
  } while( true );
}



/* Address: 0040f4a4 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void __cdecl FUN_0040f4a4(undefined4 *param_1)

{
  undefined4 uVar1;
  undefined4 *puVar2;
  
  LOCK();
  _DAT_00416382 = _DAT_00416382 + 1;
  UNLOCK();
  if (_DAT_00416382 != 0) {
    FUN_0040e590(1);
  }
  puVar2 = param_1 + -1;
  if ((undefined4 *)0x3 < param_1 && puVar2 != (undefined4 *)0x0) {
    uVar1 = *(undefined4 *)*puVar2;
    *(undefined4 *)*puVar2 = puVar2;
    *param_1 = uVar1;
  }
  LOCK();
  _DAT_00416382 = _DAT_00416382 + -1;
  UNLOCK();
  if (-1 < _DAT_00416382) {
    FUN_0040e60c(1);
  }
  return;
}



/* Address: 0040f4e4 */

void __cdecl FUN_0040f4e4(undefined4 *param_1)

{
  undefined4 *puVar1;
  undefined4 uVar2;
  
  if ((undefined4 *)0x3 < param_1) {
    puVar1 = (undefined4 *)param_1[-1];
    uVar2 = *puVar1;
    *puVar1 = param_1 + -1;
    *param_1 = uVar2;
  }
  return;
}



/* Address: 0040f4f8 */

undefined4 * __cdecl FUN_0040f4f8(int param_1,int param_2)

{
  undefined4 *puVar1;
  
  puVar1 = FUN_0040f0e4(param_1 * param_2);
  if (puVar1 == (undefined4 *)0x0) {
    return (undefined4 *)0x0;
  }
  FUN_0040d2a4((undefined1 *)puVar1,0,param_1 * param_2);
  return puVar1;
}



/* 0040f540: thunk_FUN_0040c58c; Thunk to FUN_0040c58c */

/* Address: 0040f548 */

char * __cdecl FUN_0040f548(uint *param_1)

{
  char *pcVar1;
  char *pcVar2;
  int iVar3;
  int *local_10;
  char *local_8;
  
  local_8 = (char *)0x0;
  LOCK();
  DAT_00416384 = DAT_00416384 + 1;
  UNLOCK();
  if (DAT_00416384 != 0) {
    FUN_0040e590(2);
  }
  if ((DAT_00411144 != (int *)0x0) && (param_1 != (uint *)0x0)) {
    for (local_10 = DAT_00411144; (char *)*local_10 != (char *)0x0; local_10 = local_10 + 1) {
      pcVar1 = FUN_0040c69c((char *)*local_10,'=');
      if (pcVar1 != (char *)0x0) {
        pcVar2 = FUN_0040c63c(param_1);
        if (pcVar2 == pcVar1 + -*local_10) {
          pcVar2 = FUN_0040c63c(param_1);
          iVar3 = FUN_0040f6dc((byte *)*local_10,(byte *)param_1,(int)pcVar2);
          if (iVar3 == 0) {
            local_8 = pcVar1 + 1;
            break;
          }
        }
      }
    }
  }
  LOCK();
  DAT_00416384 = DAT_00416384 + -1;
  UNLOCK();
  if (-1 < DAT_00416384) {
    FUN_0040e60c(2);
  }
  return local_8;
}



/* Address: 0040f610 */

void FUN_0040f610(void)

{
  FUN_0040f548((uint *)&DAT_0041113c);
  return;
}



/* Address: 0040f620 */

void FUN_0040f620(void)

{
  return;
}



/* Address: 0040f624 */

LPVOID __cdecl FUN_0040f624(int param_1)

{
  LPVOID lpAddress;
  int *piVar1;
  
  lpAddress = VirtualAlloc((LPVOID)0x0,param_1 + 0xffffU & 0xffff0000,0x1000,4);
  if (lpAddress == (LPVOID)0x0) {
    return (LPVOID)0xffffffff;
  }
  if ((DAT_00411140 == (int *)0x0) || (0x3ff < DAT_00411140[1])) {
    piVar1 = (int *)VirtualAlloc((LPVOID)0x0,0x1008,0x1000,4);
    if (piVar1 == (int *)0x0) {
      VirtualFree(lpAddress,0,0x8000);
      return (LPVOID)0xffffffff;
    }
    *piVar1 = (int)DAT_00411140;
    DAT_00411140 = piVar1;
    piVar1[1] = 0;
  }
  DAT_00411140[DAT_00411140[1] + 2] = (int)lpAddress;
  DAT_00411140[1] = DAT_00411140[1] + 1;
  return lpAddress;
}



/* Address: 0040f6cc */

int __cdecl FUN_0040f6cc(int param_1)

{
  return *(int *)(*(int *)(param_1 + -4) + 4) + -4;
}



/* Address: 0040f6dc */

int __cdecl FUN_0040f6dc(byte *param_1,byte *param_2,int param_3)

{
  uint uVar1;
  byte bVar2;
  int iVar3;
  uint local_4;
  
  iVar3 = 0;
  local_4 = 0;
  while( true ) {
    bVar2 = (byte)iVar3;
    if (param_3 == 0) break;
    iVar3 = FUN_0040c6f0((uint)*param_1);
    bVar2 = (byte)iVar3;
    param_1 = param_1 + 1;
    uVar1 = FUN_0040c6f0((uint)*param_2);
    local_4 = uVar1 & 0xff;
    param_2 = param_2 + 1;
    if ((bVar2 == 0) || (param_3 = param_3 + -1, bVar2 != (byte)uVar1)) break;
  }
  return bVar2 - local_4;
}



/* Address: 0040f738 */

undefined4 FUN_0040f738(void)

{
  char cVar1;
  char *pcVar2;
  undefined4 *puVar3;
  uint *puVar4;
  int iVar5;
  int iVar6;
  
  iVar5 = 1;
  cVar1 = (char)*DAT_00410cb9;
  puVar4 = DAT_00410cb9;
  while (cVar1 != '\0') {
    iVar5 = iVar5 + 1;
    pcVar2 = FUN_0040c63c(puVar4);
    puVar4 = (uint *)((int)puVar4 + (int)(pcVar2 + 1));
    cVar1 = (char)*puVar4;
  }
  if (DAT_00411144 == (undefined4 *)0x0) {
    puVar3 = FUN_0040f0e4(iVar5 << 2);
  }
  else {
    puVar3 = FUN_0040f28c(DAT_00411144,iVar5 << 2);
  }
  if (puVar3 != (undefined4 *)0x0) {
    iVar5 = 0;
    DAT_00411144 = puVar3;
    if ((char)*DAT_00410cb9 != '\0') {
      iVar6 = 0;
      puVar4 = DAT_00410cb9;
      do {
        puVar3 = FUN_0040f7fc(puVar4);
        if (puVar3 == (undefined4 *)0x0) {
          return 0xffffffff;
        }
        *(undefined4 **)(iVar6 + (int)DAT_00411144) = puVar3;
        iVar6 = iVar6 + 4;
        iVar5 = iVar5 + 1;
        pcVar2 = FUN_0040c63c(puVar4);
        puVar4 = (uint *)((int)puVar4 + (int)(pcVar2 + 1));
      } while ((char)*puVar4 != '\0');
    }
    DAT_00411144[iVar5] = 0;
    return 0;
  }
  return 0xffffffff;
}



/* Address: 0040f7fc */

undefined4 * __cdecl FUN_0040f7fc(uint *param_1)

{
  char *pcVar1;
  undefined4 *puVar2;
  
  pcVar1 = FUN_0040c63c(param_1);
  puVar2 = FUN_0040f0e4((uint)(pcVar1 + 1));
  if (puVar2 != (undefined4 *)0x0) {
    FUN_0040c2a0(puVar2,param_1,(uint)(pcVar1 + 1));
  }
  return puVar2;
}



/* 0040f86c: ExitProcess; Thunk to KERNEL32.DLL::ExitProcess */

/* 0040f878: GetCurrentThreadId; Thunk to KERNEL32.DLL::GetCurrentThreadId */

/* 0040f87e: FreeEnvironmentStringsA; Thunk to KERNEL32.DLL::FreeEnvironmentStringsA */

/* 0040f884: GetEnvironmentStrings; Thunk to KERNEL32.DLL::GetEnvironmentStrings */

/* 0040f88a: RtlUnwind; Thunk to KERNEL32.DLL::RtlUnwind */

/* 0040f890: ReadFile; Thunk to KERNEL32.DLL::ReadFile */

/* 0040f896: WriteFile; Thunk to KERNEL32.DLL::WriteFile */

/* 0040f8ae: SetFilePointer; Thunk to KERNEL32.DLL::SetFilePointer */

/* 0040f8ba: GetStdHandle; Thunk to KERNEL32.DLL::GetStdHandle */

/* 0040f902: GetVersion; Thunk to KERNEL32.DLL::GetVersion */

/* 0040f908: GlobalFree; Thunk to KERNEL32.DLL::GlobalFree */

/* 0040f914: GlobalAlloc; Thunk to KERNEL32.DLL::GlobalAlloc */

