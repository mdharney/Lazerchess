import java.util.Scanner;
import java.util.LinkedList;
public class Main{
	public static void main(String [] args){
		Scanner user_input = new Scanner(System.in);
		Board b = new Board();
		boolean gameOn = true;
		int pnum = 1;
		String type = user_input.nextLine();
		Howard howard = new Howard(2);
		Howard rupert = new Howard(1);
		b.board[4][0] = 1;
		b.board[2][6] = 1;
		while(type.equals("human")){ //2player
			b.printBoard();
			System.out.println("Player " + pnum + ", input an x,y, and slash number");
			String x = user_input.nextLine();
			if(x.equals("surrender")){
				break;
			}
			String y = user_input.nextLine();
			String slash = user_input.nextLine();
			int truex = 7 - Integer.parseInt(y);
			int truey = Integer.parseInt(x) - 1;
			int slashnum = Integer.parseInt(slash);
			if(!b.isMoveLegal(slashnum, truex, truey, pnum)){ //Commits to move if it is valid, otherwise, re-ask
				System.out.println("Move isn't valid. Go again");
				continue;
			}else{
				b.doMove(slashnum,truex,truey);
			}
			Move m = new Move(slashnum, pnum, truex, truey);
			int winnernum = b.wouldSomeoneWin(m);
			if(winnernum != 0){
				b.doMove(slashnum, truex, truey); //since wouldIwin does and undoes the move, the move needs to be inputed again since it's deleted
				System.out.println("P" + winnernum + " WINS!!");
				b.printBoard();
				break;
			}
			b.doMove(slashnum, truex, truey);
			if(pnum == 1){
				pnum++;
			}else{
				pnum--;
			}
		}
		b.board[4][0] = 1;
		b.board[2][6] = 1;
		while(type.equals("howard")){ //play against my son
			b.printBoard();
			System.out.println("Player 1, input an x,y, and slash number");
			String x = user_input.nextLine();
			if(x.equals("surrender")){
				break;
			}
			String y = user_input.nextLine();
			String slash = user_input.nextLine();
			int truex = 7 - Integer.parseInt(y);
			int truey = Integer.parseInt(x) - 1;
			int slashnum = Integer.parseInt(slash);
			if(!b.isMoveLegal(slashnum, truex, truey, pnum)){ //Commits to move if it is valid, otherwise, re-ask
				System.out.println("Move isn't valid. Go again");
				continue;
			}else{
				b.doMove(slashnum,truex,truey);
			}
			Move m = new Move(slashnum, pnum, truex, truey);
			int winnernum = b.wouldSomeoneWin(m);
			if(winnernum != 0){
				b.doMove(slashnum, truex, truey); //since wouldIwin does and undoes the move, the move needs to be inputed again since it's deleted
				if(winnernum == 1){
					System.out.println("P1 WINS!!");
				}else{
					System.out.println("Howard WINS!!");
				}
				b.printBoard();
				break;
			}
			b.doMove(slashnum, truex, truey);
			Move hm = howard.nextMove(b);
			b.doMove(hm.slash, hm.x, hm.y);
			int newx = hm.y+1;
			int newy = 7 - hm.x;
			System.out.println("Howard has selected " + newx + ", " + newy);
			//howard.bonuses = new LinkedList<String>();
			if(b.hasSomeoneWon() == 2){
				System.out.println("Howard WINS!!");
			}
		}
		while(type.equals("hh")){
			b.printBoard();
			Move hm = rupert.nextMove(b);
			b.doMove(hm.slash, hm.x, hm.y);
			int newx = hm.y+1;
			int newy = 7 - hm.x;
			System.out.println("Howard1 has selected " + newx + ", " + newy + " = " + hm.heuristicvalue);
			//howard.bonuses = new LinkedList<String>();
			if(b.hasSomeoneWon() == 1){
				System.out.println("Howard1 WINS!!");
				b.printBoard();
				break;
			}
			b.printBoard();
			hm = howard.nextMove(b);
			b.doMove(hm.slash, hm.x, hm.y);
			int newx1 = hm.y+1;
			int newy1 = 7 - hm.x;
			System.out.println("Howard2 has selected " + newx1 + ", " + newy1 + " = " + hm.heuristicvalue);
			//howard.bonuses = new LinkedList<String>();
			if(b.hasSomeoneWon() == 2){
				System.out.println("Howard2 WINS!!");
				b.printBoard();
				break;
			}
		}
	}
}
