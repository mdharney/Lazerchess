/*   							OUR REFERENCE								                    |					FOR USERS
 *  	      Y-AXIS 														                    |			      ______________ 
 *    		0 1 2 3 4 5 6                             DIRECTIONS	  |			     7|			        |
 *   	    _____________													                  |	   	 Y   6|			        |
 * 		0  |             |0                               4					|			     5|			        |
 * X	1  |             |<                               					|	   	 A   4|			        |
 * 		2  |             |                                I					|		   X   3|			        |
 * A  3  |             |                         3   <-   ->   1	|		   I   2|			        |
 * X	4  |             |                                I 				|		   S   1|______________|
 * I	5 >|             |                                					|				 1 2 3 4 5 6 7
 * S	6 0|_____________|                                2					|					 X-AXIS
 * 
 *___________________________________________________________________________________________________________________________________*/
import java.util.LinkedList;
public class Board {
int[][] board; /* '/'==1    '\'==2     ' '==0  */
boolean[][] marked;
Board(){
	this.board = new int[7][7];
	this.marked = new boolean[7][7];
	for(int i=0; i<7; i++){
		for(int j=0; j<7; j++){
			board[i][j] = 0;
			marked[i][j] = false;
		}
	}
}
public boolean moveWouldTrapMe(int x, int y, int slashnum, int myplayernum){ //opponent's move requires winning move to exit
	doMove(slashnum,x,y);											//     |_|_| that connects to left wall                         |_|_|0  connects to right wall
	if(myplayernum == 1 && board[5][1] == 1 && (wallConnects(5,1,3)|| wallConnects(5,1,4))){//     |_|_| leaving P1's only escape sealing itself off        |/|_|<  seals off P2
		undoMove(x,y);												//    >|_|/| Note: This is not checkmate, only a bad heuristic  |_|_|
		return true;												//    0|_|_|
	}
	if(myplayernum == 2 && board[1][5] == 1 && (wallConnects(1,5,1) || wallConnects(1,5,2))){
		undoMove(x,y);
		return true;
	}
	undoMove(x,y);
	return false;	
}
public boolean amITrapped(int myplayernum){
	if(myplayernum == 1 && board[5][1] == 1 && (wallConnects(5,1,3)|| wallConnects(5,1,4))){
		return true;												
	}
	if(myplayernum == 2 && board[1][5] == 1 && (wallConnects(1,5,1) || wallConnects(1,5,2))){
		return true;
	}
	return false;	
}
public boolean wouldIwin(Move m){
	doMove(m.slash,m.x,m.y);
	EndYield p1win = new EndYield(1,0,6);
	EndYield p2win = new EndYield(3,6,0);
	EndYield p1yield = lazerYield(5,0,1);
	EndYield p2yield = lazerYield(1,6,3);
	//System.out.println("p1yield " + p1yield.x + " " + p1yield.y + " " + p1yield.direction);
	if(m.player == 1 && p1yield.equals(p1win)){
		undoMove(m.x,m.y);
		return true;
	}
	if(m.player == 2 && p2yield.equals(p2win)){
		undoMove(m.x,m.y);
		return true;
	}
	undoMove(m.x,m.y);
	return false;
}
public int wouldSomeoneWin(Move m){ //returns num of winning player
	doMove(m.slash,m.x,m.y);
	EndYield p1win = new EndYield(1,0,6);
	EndYield p2win = new EndYield(3,6,0);
	EndYield p1yield = lazerYield(5,0,1);
	EndYield p2yield = lazerYield(1,6,3);
	if(p1yield.equals(p1win)){
		undoMove(m.x,m.y);
		return 1;
	}
	if(p2yield.equals(p2win)){
		undoMove(m.x,m.y);
		return 2;
	}
	undoMove(m.x,m.y);
	return 0;
}
public int hasSomeoneWon(){ //returns num of winning player
	EndYield p1win = new EndYield(1,0,6);
	EndYield p2win = new EndYield(3,6,0);
	EndYield p1yield = lazerYield(5,0,1);
	EndYield p2yield = lazerYield(1,6,3);
	if(p1yield.equals(p1win)){
		return 1;
	}
	if(p2yield.equals(p2win)){
		return 2;
	}
	return 0;
}
public Move moveThatTraps(int pnum){ //searches for a move capable of trapping a player
	Move ret = new Move(17,17,17,17);
	for(CoordinateSet cs : blankSpaces()){
		if(moveWouldTrapMe(cs.x,cs.y,1,pnum)){
			ret = new Move(1,pnum,cs.x,cs.y);
		}
		if(moveWouldTrapMe(cs.x,cs.y,2,pnum)){
			ret = new Move(2,pnum,cs.x,cs.y);
		}
		if(ret.x != 17 && !(ret.x == 5 && ret.y == 0 && ret.slash == 2) && !(ret.x == 1 && ret.y == 6 && ret.slash == 2)){
			return ret;
		}
	}
	return null;
}
public LinkedList<CoordinateSet> blankSpaces(){
	LinkedList<CoordinateSet> lst = new LinkedList<CoordinateSet>();
	for(int i=0;i<7;i++){
		for(int j=0;j<7;j++){
			if(board[i][j] == 0){
				lst.addLast(new CoordinateSet(i,j));
			}
		}
	}
	return lst;
}
void doMove(int slash, int x, int y){
	if(board[x][y] == 0){
		board[x][y] = slash;
	}
}
void undoMove(int x, int y){
	board[x][y] = 0;
}
void clearMarks(){
	for(int i=0; i<7; i++){
		for(int j=0; j<7; j++){
			marked[i][j] = false;
		}
	}
}
Move imInCheckTheyCouldWinWith(int player){ //Returns the opponent's winning move if you're in check, otherwise null
	int otherp;
	LinkedList<CoordinateSet> opponentMoveSpots = new LinkedList<CoordinateSet>();
	if(player == 1){
		opponentMoveSpots = lazerPathBlanks(1,6,3); //returns blank spaces P2's laser passes through
		otherp = 2;
	}else{
		opponentMoveSpots = lazerPathBlanks(5,0,1); //returns blank spaces P1's laser passes through
		otherp = 1;
	}
	for(CoordinateSet c : opponentMoveSpots){ // finds if there's a winning move in the blank spaces that your opponent's laser passes through
		Move m1 = new Move(1,otherp,c.x,c.y);
		Move m2 = new Move(2, otherp,c.x,c.y);
		if(wouldIwin(m1) && isMoveLegal(1,c.x,c.y,otherp)){
			return m1;
		}
		if(wouldIwin(m2) && isMoveLegal(2,c.x,c.y,otherp)){
			return m2;
		}
	}
	return null;
}
boolean wallConnects(int x, int y, int wallnum){ // Traces connecting slashes from the origin space to see if they reach wall of number wallnum(see direction index)
	int slash = board[x][y];
	marked[x][y] = true;
	if(x == 1 && y == 6 && wallnum == 6){ //p2 laser spot
		return true;
	}
	if(x == 1 && y == 6 && wallnum == 1){
		return false;
	}
	if(x == 5 && y == 0 && wallnum == 5){ //p1 laser spot
		return true;
	}
	if(x == 5 && y == 0 && wallnum == 3){
		return false;
	}
	if(x == 0 && wallnum == 4){ //upper wall
		return true;
	}
	if(y == 6 && wallnum == 1 && !(x == 0 && board[x][y] == 1)){ //right wall
		return true;
	}
	if(x == 6 && wallnum == 2){ //lower wall
		return true;
	}
	if(y == 0 && wallnum == 3 && !(x == 6 && board[x][y] == 1)){ //left wall
		return true;
	}																				
	if(slash == 1){/*   /	*/															// SLASH=1 CONNECTIONS
		if(x > 0){	//upper row															//		\/
			if(board[x - 1][y] == 2 && marked[x - 1][y] == false){ //one spot above		// 	   \/\
				if(wallConnects(x - 1,y,wallnum) == true){								//     /\
					return true;
				}
			}
		}
		if(y < 6){ // column to the right
			if(x > 0){ //upper row
				if(board[x - 1][y + 1] == 1 && marked[x - 1][y + 1] == false){ //one up one right
					if(wallConnects(x - 1, y + 1, wallnum) == true){
						return true;
					}
				}
			}
			if(board[x][y + 1] == 2 && marked[x][y + 1] == false){ //one right
				if(wallConnects(x,y+1,wallnum) == true){
					return true;
				}
			}
		}
		if(x < 6){ // bottom row
			if(board[x+1][y] == 2 && marked[x+1][y] == false){ //one down
				if(wallConnects(x+1,y,wallnum) == true){
					return true;
				}
			}
			if(y > 0){ //column to the left
				if(board[x+1][y-1] == 1 && marked[x+1][y-1] == false){ //one down one left
					if(wallConnects(x+1,y-1,wallnum) == true){
						return true;
					}
				}
			}
		}
		if(y > 0){ //column to the left
			if(board[x][y-1] == 2 && marked[x][y-1] == false){ //one left
				if(wallConnects(x,y-1,wallnum) == true){
					return true;
				}
			}
		}
	}// END OF SLASH 1, NOW SLASH 2
	if(slash == 2){/*     \     */														// SLASH=2 CONNECTIONS
		if(x > 0){ //upper row															//		  \/
			if(board[x-1][y] == 1 && marked[x-1][y] == false){ //one up					//		  /\/
				if(wallConnects(x-1,y,wallnum) == true){								//         /\
					return true;
				}
			}
			if(y > 0){ //left column
				if(board[x-1][y-1] == 2 && marked[x-1][y-1] == false){ //one up one left
					if(wallConnects(x-1,y-1,wallnum) == true){
						return true;
					}
				}
			}
		}
		if(y > 0){ //left column
			if(board[x][y-1] == 1 && marked[x][y-1] == false){ //one left
				if(wallConnects(x,y-1,wallnum) == true){
					return true;
				}
			}
		}
		if(x < 6){ //lower row
			if(board[x+1][y] == 1 && marked[x+1][y] == false){ //one down
				if(wallConnects(x+1,y,wallnum) == true){
					return true;
				}
			}
			if(y < 6){ //right column
				if(board[x+1][y+1] == 2 && marked[x+1][y+1] == false){ //one down one right
					if(wallConnects(x+1,y+1,wallnum) == true){
						return true;
					}
				}
			}
		}
		if(y < 6){ //right column
			if(board[x][y+1] == 1 && marked[x][y+1] == false){ //one right
				if(wallConnects(x,y+1,wallnum) == true){
					return true;
				}
			}
		}
	}
	return false;
}
boolean isMoveLegal(int slash, int x, int y, int playernum){ // Checks to see if move is Legal, allows game-winning rule-breaking (without overwriting other moves)
	if(board[x][y] == 0 && wouldIwin(new Move(slash,playernum,x,y))){
		return true;
	}else{
		return isMoveValid(slash,x,y);
	}
}
boolean isMoveValid(int slash, int x, int y){ //Checks to see if move is Valid, with no game-winning rule-breaking allowed
	if(board[x][y] != 0){ //space is occupied
		return false;
	}
	if((x == 5 && y == 0 && slash == 2) || (x == 1 && y == 6 && slash == 2) || (x == 4 && y == 0 && slash == 2) || (x == 2 && y == 6 && slash == 2)){ //move instantly forces illegal move
		return false;
	}
	doMove(slash, x, y); //updates board with the move
	boolean p1start = wallConnects(x,y,5);
	clearMarks();
	boolean p2start = wallConnects(x,y,6);
	clearMarks();
	boolean wallup = wallConnects(x,y,4);
	clearMarks();
	boolean wallright = wallConnects(x,y,1);
	clearMarks();
	boolean walldown = wallConnects(x,y,2);
	clearMarks();
	boolean wallleft = wallConnects(x,y,3);
//	System.out.println(p1start + " " + p2start + " " + wallup + " " + wallright + " " + walldown + " " + wallleft);
	clearMarks();
	if((wallright && wallup) || (walldown && wallleft) || (walldown && wallup) || (wallright && wallleft) || (wallup && p1start) || (wallleft && p1start) || (wallright && p2start) || (wallright && p2start)){ //Move connects 2 walls, blocking off player
		undoMove(x,y);
		return false;
	}
	undoMove(x,y);
	return true;
}
LinkedList<CoordinateSet> lazerPath(int startx, int starty, int dir){ //returns every space the laser passes through
	return lazerPathAcc(startx,starty,dir,new LinkedList<CoordinateSet>());
}
LinkedList<CoordinateSet> lazerPathAcc(int startx, int starty, int dir, LinkedList<CoordinateSet> sofar){ //lazerPath helper function
	int slash = board[startx][starty];
	sofar.addLast(new CoordinateSet(startx, starty));
	if((slash == 0 && dir == 4) || (slash == 1 && dir == 1) ||(slash == 2 && dir == 3)){ //going up if possible
		if(startx == 0){
			return sofar;
		}else{
			return lazerPathAcc(startx - 1, starty, 4, sofar);
		}
	}
	if((slash == 0 && dir == 1) || (slash == 1 && dir == 4) || (slash == 2 && dir == 2)){ //going right if possible
		if(starty == 6){
			return sofar;
		}else{
			return lazerPathAcc(startx, starty + 1, 1, sofar);
		}
	}
	if((slash == 0 && dir == 2) || (slash == 1 && dir == 3) || (slash == 2 && dir == 1)){ //going down if possible
		if(startx == 6){
			return sofar;
		}else{
			return lazerPathAcc(startx + 1, starty, 2, sofar);
		}
	}
	if((slash == 0 && dir == 3) || (slash == 1 && dir == 2) || (slash == 2 && dir == 4)){ //going left if possible
		if(starty == 0){
			return sofar;
		}else{
			return lazerPathAcc(startx, starty - 1, 3, sofar);
		}
	}
	System.out.println("Following Lazer, shouldn't get to this point");
	return sofar;
}
LinkedList<CoordinateSet> lazerPathBlanks(int startx, int starty, int dir){ //returns every EMPTY space the laser passes through
	return lazerPathBlanksAcc(startx,starty,dir,new LinkedList<CoordinateSet>());
}
LinkedList<CoordinateSet> lazerPathBlanksAcc(int startx, int starty, int dir, LinkedList<CoordinateSet> sofar){ //lazerPathBlanks helper function
	int slash = board[startx][starty];
	if(board[startx][starty] == 0){
		sofar.addLast(new CoordinateSet(startx, starty));
	}
	if((slash == 0 && dir == 4) || (slash == 1 && dir == 1) ||(slash == 2 && dir == 3)){ //going up if possible
		if(startx == 0){
			return sofar;
		}else{
			return lazerPathBlanksAcc(startx - 1, starty, 4, sofar);
		}
	}
	if((slash == 0 && dir == 1) || (slash == 1 && dir == 4) || (slash == 2 && dir == 2)){ //going right if possible
		if(starty == 6){
			return sofar;
		}else{
			return lazerPathBlanksAcc(startx, starty + 1, 1, sofar);
		}
	}
	if((slash == 0 && dir == 2) || (slash == 1 && dir == 3) || (slash == 2 && dir == 1)){ //going down if possible
		if(startx == 6){
			return sofar;
		}else{
			return lazerPathBlanksAcc(startx + 1, starty, 2, sofar);
		}
	}
	if((slash == 0 && dir == 3) || (slash == 1 && dir == 2) || (slash == 2 && dir == 4)){ //going left if possible
		if(starty == 0){
			return sofar;
		}else{
			return lazerPathBlanksAcc(startx, starty - 1, 3, sofar);
		}
	}
	System.out.println("Following Lazer, shouldn't get to this point");
	return sofar;
}
EndYield lazerYield(int startx, int starty, int dir){ //returns where the lazer ends and its orientation
	int slash = board[startx][starty];
	if((slash == 0 && dir == 4) || (slash == 1 && dir == 1) ||(slash == 2 && dir == 3)){ //going up if possible
		if(startx == 0){
			return new EndYield(dir, startx, starty);
		}else{
			return lazerYield(startx - 1, starty, 4);
		}
	}
	if((slash == 0 && dir == 1) || (slash == 1 && dir == 4) || (slash == 2 && dir == 2)){ //going right if possible
		if(starty == 6){
			if(startx == 0){
				return new EndYield(1, startx, starty);
			}
			return new EndYield(dir, startx, starty);
		}else{
			return lazerYield(startx, starty + 1, 1);
		}
	}
	if((slash == 0 && dir == 2) || (slash == 1 && dir == 3) || (slash == 2 && dir == 1)){ //going down if possible
		if(startx == 6){
			return new EndYield(dir, startx, starty);
		}else{
			return lazerYield(startx + 1, starty, 2);
		}
	}
	if((slash == 0 && dir == 3) || (slash == 1 && dir == 2) || (slash == 2 && dir == 4)){ //going left if possible
		if(starty == 0){
			if(startx == 6){
				return new EndYield(3, startx, starty);
			}
			return new EndYield(dir, startx, starty);
		}else{
			return lazerYield(startx, starty - 1, 3);
		}
	}
	System.out.println("Following Lazer, shouldn't get to this point");
	return new EndYield(dir, startx, starty);
}
void printBoard(){
	for(int i=0; i<7; i++){
		System.out.print(7-i + " ");
		if(i == 5){
			System.out.print(">");
		}else if(i == 6){
			System.out.print("0");
		}else{
			System.out.print(" ");
		}
		for(int j=0; j<7; j++){
			System.out.print("|");
			int slashnum = board[i][j];
			if(slashnum == 0){
				System.out.print(" ");
			}else if(slashnum == 1){
				System.out.print("/");
			}else{
				System.out.print("\\");
			}
		}
		System.out.print("|");
		if(i == 0){ System.out.println("0");}
		else if(i == 1){ System.out.println("<");}
		else{ System.out.println();}
	}
	System.out.println("    1 2 3 4 5 6 7");
}
}
