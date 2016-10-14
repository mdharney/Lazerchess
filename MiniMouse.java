import java.util.LinkedList;
public class MiniMouse {
	int playernum;
	int lazerx;
	int lazery;
	int lazerdir;
	int oppnum;
	int opplazerx;
	int opplazery;
	int opplazerdir;
	MiniMouse(int pn){
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
	public int simpleHeuristic(int slash, int x, int y, Board b){ //High is good. Check if it's legal before running
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
			simpleheuristic += 1000000;
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
		actual = b;
		LinkedList<CoordinateSet> actualEmpty = new LinkedList<CoordinateSet>();
		LinkedList<CoordinateSet> testPossible = new LinkedList<CoordinateSet>();
		actualEmpty = b.blankSpaces();
		testPossible = b.blankSpaces();
		int[] testedValue = new int[4];
		testedValue = minMax(true,actualEmpty,testPossible,0,actual);
		if(!b.isMoveLegal(testedValue[3], testedValue[1], testedValue[2],playernum)){
			return nextMove(b);
//		for(int kk=0; kk<actualEmpty.size();kk++){
//			if(b.isMoveLegal(1, actualEmpty.get(kk).x, actualEmpty.get(kk).y,playernum)){
//				Move returnCords2 = new Move(1,playernum,actualEmpty.get(kk).x, actualEmpty.get(kk).y);
//				return returnCords2;
//			}
//			if(b.isMoveLegal(2, actualEmpty.get(kk).x, actualEmpty.get(kk).y,playernum)){
//				Move returnCords2 = new Move(2,playernum,actualEmpty.get(kk).x, actualEmpty.get(kk).y);
//				return returnCords2;
//			}
//		}

		}
		Move returnCords = new Move(testedValue[3],playernum,testedValue[1],testedValue[2]);
		return returnCords;

	}
	public int[] minMax(boolean maxState, LinkedList<CoordinateSet> start,
			LinkedList<CoordinateSet> end,
			int terminalDepth,Board bb){
		int[] returnMove = new int[4];
		returnMove[1]=0;
		returnMove[2]=0;
		returnMove[3]=2;
		if(maxState){
			returnMove[0]= -1000001;
		}
		else{
			returnMove[0]= 1000001;
		}

		for(int i = 0; i < start.size();i++){
			Board checkBoard = new Board();
			checkBoard = bb;
			if(!checkBoard.isMoveLegal(1, start.get(i).x, start.get(i).y,playernum) && 
					!checkBoard.isMoveLegal(2, start.get(i).x, start.get(i).y,playernum)){
				continue;
			}
			int[] tempMove = new int[4];
			for(int w =1;w<3;w++){

				if(i>start.size()){
					break;
				}
				if(bb.isMoveLegal(w, start.get(i).x, start.get(i).y,playernum)){
					tempMove = split(w,maxState,start,end,terminalDepth,bb,i,checkBoard);
					if(maxState){
						if(tempMove[0]>returnMove[0]&&checkBoard.isMoveLegal(w, start.get(i).x, start.get(i).y,playernum)){
							returnMove[0] = tempMove[0];
							returnMove[1] = tempMove[1];
							returnMove[2] = tempMove[2];
							returnMove[3] = tempMove[3];
						}
					}
					else{
						if(tempMove[0]<returnMove[0]&&checkBoard.isMoveLegal(w, start.get(i).x, start.get(i).y,playernum)){
							returnMove[0] = tempMove[0];
							returnMove[1] = tempMove[1];
							returnMove[2] = tempMove[2];
							returnMove[3] = tempMove[3];
						}
					}
				}
			}
		}
		return returnMove;
	}
	public int[] split(int slash,boolean maxState,LinkedList<CoordinateSet> start,LinkedList<CoordinateSet> end,
			int terminalDepth, Board bb, int i, Board checkBoard){//board.undoMove();
		//maxState=!maxState;///////////////////////////////////
		int[] returnMove = new int[4];
		returnMove[1]=0;
		returnMove[2]=0;
		if(maxState){
			returnMove[0]= -1000001;
		}
		else{
			returnMove[0]= 1000001;
		}
		boolean checker = true;
		for(int t=0; t < end.size();t++){
			if(checkBoard.isMoveLegal(1, end.get(t).x, end.get(t).y, playernum)){
				checker = false;
				break;
			}
			if(checkBoard.isMoveLegal(1, end.get(t).x, end.get(t).y, playernum)){
				checker = false;
				break;
			}
		}
		if(checker){
			if(maxState){
				returnMove[0]=-1000002;
			}
			else{
				returnMove[0]=1000002;
			}
			returnMove[1]=0;
			returnMove[2]=0;
			return returnMove;
		}
		checkBoard.doMove(slash,start.get(i).x,start.get(i).y);
		CoordinateSet dummy = new CoordinateSet(0,0);
		//System.out.println(i);
		//System.out.print(end.size());
		dummy = end.get(i);
		//end.remove(dummy);
		if(terminalDepth <= 8){
			for (int a =0; a< end.size();a++){
				
				int xx = end.get(a).x;
				int yy = end.get(a).y;
				int checkVal= simpleHeuristic(slash,xx,yy,checkBoard);
				if(maxState){
					if(checkVal>returnMove[0]&&checkBoard.isMoveLegal(slash, start.get(i).x, start.get(i).y,playernum)){
						returnMove[0] = checkVal;
						returnMove[1] = xx;
						returnMove[2] = yy;
						returnMove[3] = slash;
					}
				}
				else{
					if(checkVal<returnMove[0]&&checkBoard.isMoveLegal(slash, start.get(i).x, start.get(i).y,playernum)){
						returnMove[0] = checkVal;
						returnMove[1] = xx;
						returnMove[2] = yy;
						returnMove[3] = slash;
					}
				}
			}
			checkBoard.undoMove(dummy.x, dummy.y);
			//end.add(i,dummy);
			//System.out.println( returnMove);
			return returnMove;
		}
		else{
			for(int a= 0; a< end.size();a++){
				int[] tempMove = new int[4];
				terminalDepth=terminalDepth+1;
				tempMove = minMax(!maxState, end,end,terminalDepth,checkBoard);
				int xx = end.get(a).x;
				int yy = end.get(a).y;
				int checkVal= simpleHeuristic(slash,xx,yy,checkBoard);

				if(maxState){
					if(tempMove[0]>returnMove[0]&&checkBoard.isMoveLegal(slash, start.get(i).x, start.get(i).y,playernum)){
						returnMove[0] = tempMove[0];
						returnMove[1] = tempMove[1];
						returnMove[2] = tempMove[2];
						returnMove[3] = tempMove[3];
					}
				}
				else{
					if(tempMove[0]<returnMove[0]&&checkBoard.isMoveLegal(slash, start.get(i).x, start.get(i).y,playernum)){
						returnMove[0] = tempMove[0];
						returnMove[1] = tempMove[1];
						returnMove[2] = tempMove[2];
						returnMove[3] = tempMove[3];
					}
				}
				
			}
			checkBoard.undoMove(dummy.x, dummy.y);
			//end.add(i,dummy);
			//System.out.println( returnMove);
			return returnMove;
		}

	}
	public Move nextMove(Board b){
		LinkedList<Move> heuristicMoves = new LinkedList<Move>();
		for(CoordinateSet cs : b.blankSpaces()){
			Move m = new Move(1,playernum,cs.x,cs.y);
			if(b.isMoveLegal(1, cs.x, cs.y, playernum)){
				m.heuristicvalue = simpleHeuristic(1,cs.x,cs.y,b);
				heuristicMoves.addLast(m);
			}
			Move m2 = new Move(2,playernum,cs.x,cs.y);
			if(b.isMoveLegal(2, cs.x, cs.y, playernum)){
				m2.heuristicvalue = simpleHeuristic(2,cs.x,cs.y,b);
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
