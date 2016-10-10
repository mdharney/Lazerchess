import java.util.LinkedList;
public class Howard { // Reflex / Heuristic AI
	int playernum;
	int lazerx;
	int lazery;
	int lazerdir;
	int oppnum;
	int opplazerx;
	int opplazery;
	int opplazerdir;
	Howard(int pn){
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
		System.out.println(bm.heuristicvalue);
		/*for(String s : bonuses){
			System.out.println(s);
		}*/
		return bm;
	}
	public int simpleHeuristic(int slash, int x, int y, Board b){ //High is good. Check if it's legal before running
		Move m = new Move(slash, playernum, x, y);
		if(b.wouldIwin(m)){
			return 1000000;
		}
		if(b.wouldSomeoneWin(m)== oppnum){
			return -1000000;
		}
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
		if(b.imInCheckTheyCouldWinWith(playernum) != null){ //if it blocks check, *1000
			Move checkmove = b.imInCheckTheyCouldWinWith(playernum);
			//System.out.println(checkmove.x + "," + checkmove.y + "," + checkmove.slash);
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
			/*if(x==1&&y==1&&slash==1){
				System.out.println("check opponent");
			}*/
		}
		b.undoMove(x, y);
		if(b.moveWouldTrapMe(x, y, slash, playernum)){ //if you put yourself in trap
				simpleheuristic -= 100;
				/*if(x==2&&y==5&&slash==1){
					System.out.println("trap myself");
				}*/
		}
		if(!b.amITrapped(oppnum) && b.moveWouldTrapMe(x, y, slash, oppnum)){ //if you trap opponent
				simpleheuristic += 100;
				/*if(x==4&&y==6&&slash==1){
					System.out.println("trap opponent");
				}*/
		}
		Move mt = b.moveThatTraps(playernum);
		if(mt != null){ 													//if it blocks trap
			//System.out.println(mt.x + " " + mt.y + " " + mt.slash + " " + playernum);
			if(mt.x == x && mt.y == y && mt.slash != slash){
				simpleheuristic += 100;
				/*if(x==2&&y==5&&slash==1){
					System.out.println("favblocks trap");
				}*/
			}
		}
		/*if(x == 2 && y == 5 && slash == 1){
			System.out.println(simpleheuristic + " myfav");
		}*/
		return simpleheuristic;
	}
}
