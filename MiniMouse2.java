import java.util.LinkedList;
import java.util.ArrayList;
public class MiniMouse2 {
	int playernum;
	int lazerx;
	int lazery;
	int lazerdir;
	int oppnum;
	int opplazerx;
	int opplazery;
	int opplazerdir;
	MiniMouse2(int pn){
		playernum = pn;
		if(pn == 1){
			oppnum = 2;
			lazerx = 5;
			lazery = 0;
			lazerdir = 1;
			opplazerx = 1;
			opplazery = 6;
			opplazerdir = 3;
		}else{
			oppnum = 1;
			lazerx = 1;
			lazery = 6;
			lazerdir = 3;
			opplazerx = 5;
			opplazery = 0;
			opplazerdir = 1;
		}
	}
	public int simpleHeuristic(int slash, int x, int y, Board b, int playernum){ //High is good. Check if it's legal before running
		Move m = new Move(slash, playernum, x, y);

		b.doMove(slash, x, y);
		EndYield lazerp1 = b.lazerYield(5, 0, 1);
		EndYield lazerp2 = b.lazerYield(1, 6, 3);
		b.undoMove(x, y);
		int userp1x;
		int userp2x;
		int userp1y;
		int userp2y;
		if(playernum == 1){			//	high|
			userp1x = lazerp1.y + 1;//		|
			userp1y = 7 - lazerp1.x;//		|
			userp2x = lazerp2.y + 1;//	low	|__________
			userp2y = 7 - lazerp1.x;//		low		high
		}else{
			userp1x = lazerp1.x + 1;//	high_______low
			userp1y = 7 - lazerp1.y;//				|low
			userp2x = lazerp2.x + 1;//				|
			userp2y = 7 - lazerp2.y;//				|high
		}
		int simpleheuristic = (userp1x * userp1y) + (userp2x * userp2y);
		if(b.wouldIwin(m)){
			simpleheuristic += 1000;
		}
		if(b.wouldSomeoneWin(m)== oppnum){
			simpleheuristic += -1000000;
		}
		if(b.imInCheckTheyCouldWinWith(playernum) != null){ //if it blocks check, *1000
			Move checkmove = b.imInCheckTheyCouldWinWith(playernum);
			b.doMove(slash, x, y);
			if(b.imInCheckTheyCouldWinWith(playernum) == null){
				simpleheuristic = simpleheuristic * 1000;
			}
			b.undoMove(x, y);
		}
		b.doMove(slash,x,y);
		if(b.imInCheckTheyCouldWinWith(playernum) != null){//if you put yourself in check
			b.undoMove(x, y);
			return -1000;
		}
		if(b.imInCheckTheyCouldWinWith(oppnum) != null){ //if you check opponent
			simpleheuristic += 50;
		}
		b.undoMove(x, y);
		if(b.moveWouldTrapMe(x, y, slash, playernum)){ //if you put yourself in trap
			simpleheuristic -= 200;

		}
		if(!b.amITrapped(oppnum) && b.moveWouldTrapMe(x, y, slash, oppnum)){ //if you trap opponent
			simpleheuristic += 200;

		}
		Move mt = b.moveThatTraps(playernum);
		if(mt != null){ 													//if it blocks trap
			if(mt.x == x && mt.y == y && mt.slash != slash){
				simpleheuristic += 200;
			}
		}
		return simpleheuristic;
	}

	public Move CalculateMove(Board b){
		Board actual = new Board();
		Board actual2 = new Board();
		Board actual3 = new Board();
		Board actual4 = new Board();
		actual = b;
		actual2=b;
		actual3=b;
		actual4=b;
		LinkedList<CoordinateSet> actualEmpty = new LinkedList<CoordinateSet>();
		LinkedList<CoordinateSet> testPossible = new LinkedList<CoordinateSet>();
		LinkedList<CoordinateSet> testPossible2 = new LinkedList<CoordinateSet>();

		actualEmpty = b.blankSpaces();
		Move bestM2=new Move(1,playernum,1,1);
		int MaxHue2 = -1000001;
		ArrayList<Integer> fullMovesH = new ArrayList<Integer>();
		ArrayList<Move> fullMoves = new ArrayList<Move>();
		for(int q =0; q<actualEmpty.size();q++){//out Max
			if(actual.isMoveLegal(1,actualEmpty.get(q).x, actualEmpty.get(q).y, playernum)){
				actual.doMove(1, actualEmpty.get(q).x, actualEmpty.get(q).y);
				testPossible = actual.blankSpaces();
				
				int MinHue = 1000001;
				Move worstM= new Move(1,playernum,1,1);;
				ArrayList<Move> movess = new ArrayList<Move>();
				ArrayList<Integer> movessH = new ArrayList<Integer>();
				for(int w =0; w<testPossible.size();w++){//min
					if(actual.isMoveLegal(1,testPossible.get(w).x, testPossible.get(w).y, playernum)){
						actual.doMove(1,testPossible.get(w).x, testPossible.get(w).y);
						testPossible2=actual.blankSpaces();
						
						int MaxHue = -1000001;
						Move bestM= new Move(1,playernum,1,1);;// = new Move(1,playernum,1,1);
						for(int u =0; u<testPossible2.size();u++){//max
							//left
							if(actual.isMoveLegal(1, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(1,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
							//right
							if(actual.isMoveLegal(2, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(2,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
						}
						movess.add(bestM);
						movessH.add(MaxHue);
						actual.undoMove(testPossible.get(w).x, testPossible.get(w).y);
					}
					if(actual.isMoveLegal(2,testPossible.get(w).x, testPossible.get(w).y, playernum)){
						actual.doMove(2,testPossible.get(w).x, testPossible.get(w).y);
						testPossible2=actual.blankSpaces();
						
						int MaxHue = -1000000;
						Move bestM= new Move(1,playernum,1,1);;// = new Move(1,playernum,1,1);
						for(int u =0; u<testPossible2.size();u++){//max
							//left
							if(actual.isMoveLegal(1, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(1,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
							//right
							if(actual.isMoveLegal(2, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(2,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
						}
						movess.add(bestM);
						movessH.add(MaxHue);
						actual.undoMove(testPossible.get(w).x, testPossible.get(w).y);
					}
					
				}
				for(int number =0; number<movess.size();number++){
					if(movessH.get(number)<MinHue&&actual.isMoveLegal(movess.get(number).slash, movess.get(number).x, movess.get(number).y, oppnum)){
				/*	if(simpleHeuristic(movess.get(number).slash,movess.get(number).x,movess.get(number).y,actual,oppnum)<MinHue&&
							actual.isMoveLegal(movess.get(number).slash, movess.get(number).x, movess.get(number).y, oppnum)){
					*/
						MinHue=simpleHeuristic(movess.get(number).slash,movess.get(number).x,movess.get(number).y,actual,oppnum);
						worstM=movess.get(number);
					}
				}
				fullMoves.add(worstM);
				fullMovesH.add(MinHue);
				actual.undoMove(actualEmpty.get(q).x, actualEmpty.get(q).y);
				
			}
			if(actual.isMoveLegal(2,actualEmpty.get(q).x, actualEmpty.get(q).y, playernum)){
				actual.doMove(2, actualEmpty.get(q).x, actualEmpty.get(q).y);
				testPossible = actual.blankSpaces();
				
				int MinHue = 1000001;
				Move worstM =new Move(1,playernum,1,1);;
				ArrayList<Move> movess = new ArrayList<Move>();
				ArrayList<Integer> movessH = new ArrayList<Integer>();
				for(int w =0; w<testPossible.size();w++){//min
					if(actual.isMoveLegal(1,testPossible.get(w).x, testPossible.get(w).y, playernum)){
						actual.doMove(1,testPossible.get(w).x, testPossible.get(w).y);
						testPossible2=actual.blankSpaces();
						
						int MaxHue = -1000001;
						Move bestM=new Move(1,playernum,1,1);;// = new Move(1,playernum,1,1);
						for(int u =0; u<testPossible2.size();u++){//max
							//left
							if(actual.isMoveLegal(1, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(1,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
							//right
							if(actual.isMoveLegal(2, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(2,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
						}
						movess.add(bestM);
						movessH.add(MaxHue);
						actual.undoMove(testPossible.get(w).x, testPossible.get(w).y);
					}
					if(actual.isMoveLegal(2,testPossible.get(w).x, testPossible.get(w).y, playernum)){
						actual.doMove(2,testPossible.get(w).x, testPossible.get(w).y);
						testPossible2=actual.blankSpaces();
						
						int MaxHue = -1000000;
						Move bestM= new Move(1,playernum,1,1);// = new Move(1,playernum,1,1);
						for(int u =0; u<testPossible2.size();u++){//max
							//left
							if(actual.isMoveLegal(1, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(1, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(1,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
							//right
							if(actual.isMoveLegal(2, testPossible2.get(u).x, testPossible2.get(u).y,playernum)){
								if(simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum)>MaxHue){
									MaxHue = simpleHeuristic(2, testPossible2.get(u).x, testPossible2.get(u).y, actual,playernum);
									bestM = new Move(2,playernum,testPossible2.get(u).x, testPossible2.get(u).y);
								}
							}
						}
						movess.add(bestM);
						movessH.add(MaxHue);
						actual.undoMove(testPossible.get(w).x, testPossible.get(w).y);
					}
					
				}
				for(int number =0; number<movess.size();number++){
					if(movessH.get(number)<MinHue&&actual.isMoveLegal(movess.get(number).slash, movess.get(number).x, movess.get(number).y, oppnum)){
				/*	if(simpleHeuristic(movess.get(number).slash,movess.get(number).x,movess.get(number).y,actual,oppnum)<MinHue&&
							actual.isMoveLegal(movess.get(number).slash, movess.get(number).x, movess.get(number).y, oppnum)){
							*/
						MinHue=simpleHeuristic(movess.get(number).slash,movess.get(number).x,movess.get(number).y,actual,oppnum);
						worstM=movess.get(number);
					}
				}
				fullMoves.add(worstM);
				fullMovesH.add(MinHue);
				actual.undoMove(actualEmpty.get(q).x, actualEmpty.get(q).y);
				
			}
			
					
			}
			for(int nn = 0; nn<fullMoves.size();nn++){
				if(fullMovesH.get(nn)>MaxHue2&&actual.isMoveLegal(fullMoves.get(nn).slash, fullMoves.get(nn).x, fullMoves.get(nn).y, playernum)){
			/*	if(simpleHeuristic(fullMoves.get(nn).slash,fullMoves.get(nn).x,fullMoves.get(nn).y,actual,playernum)>MaxHue2&&
						actual.isMoveLegal(fullMoves.get(nn).slash, fullMoves.get(nn).x, fullMoves.get(nn).y, playernum)){
				*/
					MaxHue2 =simpleHeuristic(fullMoves.get(nn).slash,fullMoves.get(nn).x,fullMoves.get(nn).y,actual,playernum);
					bestM2=fullMoves.get(nn);
				}
			}
			
			if(!b.isMoveLegal(bestM2.slash, bestM2.x, bestM2.y, bestM2.player)){
				//return nextMove(b);
			}
			
			
			
			return bestM2;
		
		}
		
		
		
	public Move nextMove(Board b){
		LinkedList<Move> heuristicMoves = new LinkedList<Move>();
		for(CoordinateSet cs : b.blankSpaces()){
			Move m = new Move(1,playernum,cs.x,cs.y);
			if(b.isMoveLegal(1, cs.x, cs.y, playernum)){
				m.heuristicvalue = simpleHeuristic(1,cs.x,cs.y,b,playernum);
				heuristicMoves.addLast(m);
			}
			Move m2 = new Move(2,playernum,cs.x,cs.y);
			if(b.isMoveLegal(2, cs.x, cs.y, playernum)){
				m2.heuristicvalue = simpleHeuristic(2,cs.x,cs.y,b,playernum);
				heuristicMoves.addLast(m2);
			}
		}
		Move bm = heuristicMoves.getFirst(); //stands for best move ... no really ... totally not intentional...okay maybe a little bit
		for(Move m : heuristicMoves){
			if(m.heuristicvalue > bm.heuristicvalue){
				bm = m;
			}
		}
		//System.out.println(bm.heuristicvalue);
		/*for(String s : bonuses){
			System.out.println(s);
		}*/
		return bm;
	}
		
	}

