//$Id: Assembler.java,v 1.6 1996/11/14 22:12:56 dubuc Exp $

/*
$Log: Assembler.java,v $
Revision 1.6  1996/11/14 22:12:56  dubuc
Puesto resolvedor de etiqueta en instruccion ldi

Revision 1.5  1996/11/13 13:44:46  dubuc
Finalizacion de proyecto en esta revision, se igualan todas las revisiones
a la 1.5

*/

package Assembler;

import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.*;
import binContainer.*;
import memory.*;

public class Assembler {

	StringTokenizer stok;
	String [] lines;
	int clines,numLab;
	OpcodeTable [] opcodes;
	LabelTable []  labels;
	static Opcodes oplist = new Opcodes();
	OpcodeTable regOp;

	public int numErr, numOp;
	public boolean assembOk, debug;
	public ErrorMessage [] errors;

	final int	DEC = 1;
	final int	HEX = 2;
	final int	BIN = 3;

	public Assembler() {
		this(false);
	}

	public Assembler(boolean deb) {
		this.debug = deb;
		this.assembOk = false;
	}

	public boolean assemble(String st) {

		assembOk = false;
		initLinesBuffer(st);
		makeTable();
		resolveLabels();

		if(debug) showBin();
		
		if(numErr > 0){
			if (debug) showErr();
			return true;
		}
		else {
			assembOk = true;
			return false;
		}
	}

	private void showErr(){
		for(int i = 0 ; i < numErr; i++)
			System.out.println("Error on Line "+(errors[i].linNo+1)+":"+errors[i].msg);
	}

	public String [] errMsgArray(){
		if(!assembOk){
			String [] stArr = new String[numErr];
			for(int i = 0 ; i < numErr; i++)
				stArr[i] = "Error on Line "+(errors[i].linNo+1)+":"+errors[i].msg;
			return stArr;
		}
		else
			return new String[0];
	}

	public String [] binArray(int dataRep){
		if(assembOk){
			String [] stArr = new String[numOp];
			for(int i = 0 ; i < numOp; i++){
				DmnByte b = new DmnByte(opcodes[i].dir);
				Word w = new Word(opcodes[i].opcode);
				switch(dataRep) {
				case HEX:
					stArr[i] = b.toHexString()+":"+w.toHexString();
					break;
				case DEC:
					stArr[i] = b.toHexString()+":"+w.toString();
					break;
				case BIN:
					stArr[i] = b.toHexString()+":"+w.toBinaryString();
					break;
				default:
					stArr[i] = "";
				}
			}
			return stArr;
		}
		else
			return new String[0];
	}

	public void putOnMemory(InstMemory iMem) {
		if(assembOk){
			for(int i = 0; i < numOp; i++) 
				if(i <= 0xff)
					iMem.writeMemory(new Word(opcodes[i].opcode),i);
			if(numOp <= 0xff)
				for(int i = numOp; i <= 0xff; i++)
					iMem.writeMemory(new Word(0),i);
		}
	}

	private void showBin() {
		for(int i = 0; i < numOp; i++) {
			Word w = new Word(opcodes[i].opcode);
			System.out.println(opcodes[i].dir + ":nemon:"+ w.toHexString());
		}
	}

	private void initLinesBuffer(String st) {
		stok = new StringTokenizer(st,"\n");
		clines = stok.countTokens();
		lines = new String[clines];
		opcodes = new OpcodeTable[clines];
		labels = new LabelTable[clines];
		errors = new ErrorMessage[clines];
		numOp = numLab = numErr = 0;
		int i = 0;
		while (stok.hasMoreTokens()){
			lines[i] =  stok.nextToken().toUpperCase();
			i++;
		}
	}



	private void makeTable() {
		String tmpSt, nenmo;
		StringBuffer tmpStb;
		int Dir = 0, cnt1, cnt2, llin, x;

		for(cnt1 = 0; cnt1 < clines;cnt1++) {
			llin = lines[cnt1].length();
			tmpSt = new String(lines[cnt1]);
			tmpSt = tmpSt.replace('\t',' ');
			tmpStb = new StringBuffer(elimChar(tmpSt,' '));
			if(tmpStb.charAt(0) != ';') {
				stok = new StringTokenizer(tmpStb.toString(),":");
				tmpSt = stok.nextToken();
				if(tmpSt.compareTo(tmpStb.toString()) != 0){
					tmpSt = elimChar(tmpSt,' ');
					tmpSt = cleanSpaces(tmpSt);
					labels[numLab] = new LabelTable(tmpSt,Dir);
					numLab++;
					try {
						tmpSt = stok.nextToken();
					} catch (java.util.NoSuchElementException e){
						tmpSt = new String();
					}
				}
				if(tmpSt.length() > 0) {
					stok = new StringTokenizer(tmpSt," ");
					nenmo = stok.nextToken();
					this.searchNenm(nenmo,cnt1,Dir);
					if((regOp.opval != 0xb)&&(regOp.opval != 0xc)&&(regOp.opval != 16)){

						tmpSt = elimChar(tmpSt,' ');

						cnt2 = 0;
						while((cnt2<tmpSt.length())&&(tmpSt.charAt(cnt2)!=' '))
							cnt2++;
						if(cnt2 < tmpSt.length()){
							tmpSt = tmpSt.substring(cnt2);

							this.resolveOperands(tmpSt,cnt1);
						}
						else { // Error
							errors[numErr] = new ErrorMessage(4,cnt1);
							numErr ++;
						}
					}
					else if (regOp.opval == 0xc) {
						regOp.opcode +=  0xd00;
					}
					opcodes[Dir] = regOp;
					Dir++;
				}
			}
		}
	}



	private String elimChar(String stIn,char ch){
		int cnt2= 0;
		while((cnt2 < stIn.length())&&(stIn.charAt(cnt2) == ch)) cnt2++;
		if(cnt2 < stIn.length())
			return stIn.substring(cnt2);
		else
			return stIn;
	}

	private String fromThisChar(String stIn,char ch){
		int cnt2= 0;
		while((cnt2 < stIn.length())&&(stIn.charAt(cnt2) != ch)) cnt2++;
		if(cnt2 < stIn.length())
			return stIn.substring(cnt2);
		else
			return stIn;
	}

	private void resolveLabels() {
		for(int i = 0; i < numOp; i ++) 
			if(opcodes[i].label != null) {
				boolean finded = false;
				for(int j = 0; j < numLab; j++){
					if(opcodes[i].label.compareTo(labels[j].label) == 0){
						opcodes[i].opcode += labels[j].dir;
						finded = true;
					}
				}
				if(!finded) {
					errors[numErr] = new ErrorMessage(2,opcodes[i].nlin);
					numErr ++;
				}
			}
	}

	private void resolveOperands(String opSt, int nlin) {
		switch(regOp.opval) {
			case 0x0: //ADD
			case 0x1: //SUB
			case 0x2: //AND
			case 0x3: //OR
			case 0x5: //SHT
			{//ADD
				opSt = elimChar(opSt,' ');
				if((opSt.charAt(0) != ';')&&(opSt.length() != 0)){
					String tmp;
					int nOp = 0;
					stok = new StringTokenizer(opSt,";");
					opSt = stok.nextToken();
					stok = new StringTokenizer(opSt,", ");
					for (nOp = 0; nOp < 3; nOp++){
						try {
							tmp = stok.nextToken();
							int regv = regNameVerif(tmp,nlin);
							if(nOp == 0)
								regOp.opcode += regv << 8;
							else if (nOp == 1)
								regOp.opcode += regv << 4;
							else if (nOp == 2)
								regOp.opcode += regv;
						} catch (java.util.NoSuchElementException e) {
							// Tengo menos de tres Operandos
							errors[numErr] = new ErrorMessage(1,nlin);
							numErr ++;
						}
					}
					if(stok.hasMoreTokens()){ //Tengo mas de Tres Operandos
						errors[numErr] = new ErrorMessage(1,nlin);
						numErr ++;
					}
				}
				else {
					errors[numErr] = new ErrorMessage(1,nlin);
					numErr ++;
				}
			}break;
			case 0x4: //NOT
			case 0x8: //NTS
			case 0x9: //ZTS
			{
				opSt = elimChar(opSt,' ');
				if((opSt.charAt(0) != ';')&&(opSt.length() != 0)){
					String tmp;
					int nOp = 0;
					stok = new StringTokenizer(opSt,";");
					opSt = stok.nextToken();
					stok = new StringTokenizer(opSt,",");
					for (nOp = 0; nOp < 2; nOp++){
						try {
							tmp = stok.nextToken();
							int regv = regNameVerif(tmp,nlin);
							if(nOp == 0){
								if(regOp.opval == 4)
									regOp.opcode += regv << 8;
								else
									regOp.opcode += regv << 4;
							}
							else if (nOp == 1){
								if(regOp.opval == 4)
									regOp.opcode += regv << 4;
								else
									regOp.opcode += regv;
							}
						} catch (java.util.NoSuchElementException e) {
							// Tengo menos de dos Operandos
							errors[numErr] = new ErrorMessage(1,nlin);
							numErr ++;
						}
					}
					if(stok.hasMoreTokens()){ //Tengo mas de dos Operandos
						errors[numErr] = new ErrorMessage(1,nlin);
						numErr ++;
					}
				}
				else {
					errors[numErr] = new ErrorMessage(1,nlin);
					numErr ++;
				}
			}break;
			case 0xe: //LDR
			case 0xf: //STR
			{
				opSt = elimChar(opSt,' ');
				if((opSt.charAt(0) != ';')&&(opSt.length() != 0)){
					String tmp;
					int nOp = 0;
					stok = new StringTokenizer(opSt,";");
					opSt = stok.nextToken();
					stok = new StringTokenizer(opSt,", ");
					for (nOp = 0; nOp < 2; nOp++){
						try {
							tmp = stok.nextToken();
							int regv = regNameVerif(tmp,nlin);
							if(nOp == 0){
								if(regOp.opval == 0xe)
									regOp.opcode += regv << 8;
								else
									regOp.opcode += regv << 4;
							}
							else if (nOp == 1){
								if(regOp.opval == 0xe)
									regOp.opcode += regv << 4;
								else
									regOp.opcode += regv;
							}
						} catch (java.util.NoSuchElementException e) {
							// Tengo menos de dos Operandos
							errors[numErr] = new ErrorMessage(1,nlin);
							numErr ++;
						}
					}
					if(stok.hasMoreTokens()){ //Tengo mas de dos Operandos
						errors[numErr] = new ErrorMessage(1,nlin);
						numErr ++;
					}
				}
				else {
					errors[numErr] = new ErrorMessage(1,nlin);
					numErr ++;
				}
			}break;
			case 0xd: //LDI
			{
				opSt = elimChar(opSt,' ');
				if((opSt.charAt(0) != ';')&&(opSt.length() != 0)){
					String tmp;
					int nOp = 0;
					stok = new StringTokenizer(opSt,";");
					opSt = stok.nextToken();
					stok = new StringTokenizer(opSt,", ");
					for (nOp = 0; nOp < 2; nOp++){
						try {
							tmp = stok.nextToken();
							if(nOp == 0){
								int regv = regNameVerif(tmp,nlin);
								regOp.opcode += regv << 8;
							}
							else if (nOp == 1){
								opSt = fromThisChar(opSt,'#');
								if(opSt.charAt(0) == '#'){
									opSt = opSt.substring(1);
									opSt = cleanSpaces(opSt);
									try {
										int dir = Integer.parseInt(opSt,16);
										if((dir < 0) || (dir > 0xff)) {
											errors[numErr] = new ErrorMessage(8,nlin);
											numErr ++;
										}
										else
											regOp.opcode += dir;
									}catch (java.lang.NumberFormatException e) {
										errors[numErr] = new ErrorMessage(1,nlin);
										numErr ++;
									}
								}
								else { //Etiqueta
									tmp = elimChar(tmp,' ');
									tmp = cleanSpaces(tmp);
									regOp.label = tmp;
								}
							}
						} catch (java.util.NoSuchElementException e) {
							// Tengo menos de dos Operandos
							errors[numErr] = new ErrorMessage(1,nlin);
							numErr ++;
						}
					}
					if(stok.hasMoreTokens()){ //Tengo mas de dos Operandos
						errors[numErr] = new ErrorMessage(1,nlin);
						numErr ++;
					}
				}
				else {
					errors[numErr] = new ErrorMessage(1,nlin);
					numErr ++;
				}
			}break;
			case 0xa: //JCN
			{
				opSt = elimChar(opSt,' ');
				if((opSt.charAt(0) != ';')&&(opSt.length() != 0)){
					stok = new StringTokenizer(opSt,";");
					opSt = stok.nextToken();
					opSt = fromThisChar(opSt,'@');
					if(opSt.charAt(0) == '@'){
						opSt = opSt.substring(1);
						opSt = cleanSpaces(opSt);
						try {
							int dir = Integer.parseInt(opSt,16);
							if((dir < 0) || (dir > 0xff)) {
								errors[numErr] = new ErrorMessage(7,nlin);
								numErr ++;
							}
							else
								regOp.opcode += (0xf00 + dir);
						}catch (java.lang.NumberFormatException e) {
							errors[numErr] = new ErrorMessage(6,nlin);
							numErr ++;
						}
					}
					else {
						opSt = elimChar(opSt,' ');
						opSt = cleanSpaces(opSt);
						regOp.opcode += 0xf00;
						regOp.label = opSt;
					}
				}
				else {
					errors[numErr] = new ErrorMessage(1,nlin);
					numErr ++;
				}
		}break;
		}
	}

	private String cleanSpaces(String st){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < st.length(); i++)
			if(st.charAt(i) != ' ')
				sb.append(st.charAt(i));
		return sb.toString();
	}

	private int regNameVerif(String rSt, int nlin){
		rSt = elimChar(rSt,' ');
		if((rSt.length() > 1)&&(rSt.charAt(0) == 'P')&&(rSt.charAt(1) == 'S')){
			return 14;
		}
		else if(rSt.charAt(0) == 'R'){
			rSt = rSt.substring(1);
				try {
					int dir = Integer.parseInt(rSt,16);
					if((dir < 0) || (dir > 0xe)) {
						errors[numErr] = new ErrorMessage(3,nlin);
						numErr ++;
						return 0;
					}
					else
						return dir;
				}catch (java.lang.NumberFormatException e) {
					errors[numErr] = new ErrorMessage(1,nlin);
					numErr ++;
					return 0;
				}
		}
		else {// Error
				errors[numErr] = new ErrorMessage(3,nlin);
				numErr ++;
				return 0;
		}
	}

	private void searchNenm(String nenmo, int lnum, int dir) {
		int i = 0;
		while((i < 14) && (nenmo.compareTo(oplist.oplst[i].mnemon) != 0)) i++;
		if(i == 14) {
			errors[numErr] = new ErrorMessage(0,lnum);
			regOp = new OpcodeTable(dir,16,lnum,null);
			numErr++;
		}
		else {
			regOp = new OpcodeTable(dir,oplist.oplst[i].opval,lnum,null);
		}
		numOp ++;
	}
}


class OpcodeTable {
	public int dir;
	public int opcode;
	public int opval;
	public String label;
	public int nlin;

	public OpcodeTable(int d, int o, int n, String s) {
		dir = d;
		opval = o;
		opcode = opval << 12;
		nlin = n;
		label = s;
	}
}

class LabelTable {
	public int dir;
	public String label;

	public LabelTable(String l, int d){
		label = l;
		dir = d;
	}
}

class ErrorMessage {
	public String msg;
	public int linNo;

	private static String [] msgs = {
		"No such nenmonic in DMN architecture.",        // 0
		"Syntax Error.",                                // 1
		"No such label in assembler program.",          // 2
		"No such register in DMN Architecture.",        // 3
		"Operands missings in instruction.",            // 4
		"Too much operands in instruction.",            // 5
		"Invalid address format in JCN instrucction.",  // 6
		"Address out of range in JCN instrucction.",    // 7
		"Value out of range in LDI instrucction."       // 8
	};


	public ErrorMessage(int errNo, int ln) {
		try{
			msg = this.msgs[errNo];
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			msg = this.msgs[0];
		}
		linNo = ln;
	}
}


class Opcode {
	public String mnemon;
	public int opval;
	public int oppos;

	Opcode(String st, int ov, int op){
		mnemon = st;
		opval = ov;
		oppos = op;
	}
}

class Opcodes {
	public static Opcode [] oplst = new Opcode[14];
	
	public Opcodes() {
		oplst[0]  = new Opcode("ADD",0x0,1);
		oplst[1]  = new Opcode("SUB",0x1,2);
		oplst[2]  = new Opcode("AND",0x2,3);
		oplst[3]  = new Opcode("OR",0x3,4);
		oplst[4]  = new Opcode("NOT",0x4,5);
		oplst[5]  = new Opcode("SHT",0x5,6);
		oplst[7]  = new Opcode("ZTS",0x8,7);
		oplst[6]  = new Opcode("NTS",0x9,8);
		oplst[8]  = new Opcode("JCN",0xa,9);
		oplst[9]  = new Opcode("RTI",0xb,10);
		oplst[10] = new Opcode("HLT",0xc,11);
		oplst[11] = new Opcode("LDI",0xd,12);
		oplst[12] = new Opcode("LDR",0xe,13);
		oplst[13] = new Opcode("STR",0xf,14);
	};
}
