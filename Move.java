import java.util.LinkedList;
public class Move {
int slash;
int player;
int x;
int y;
int heuristicvalue;
Move(int slashnum,int playernum,int xspot,int yspot){
	slash = slashnum;
	player = playernum;
	x = xspot;
	y = yspot;
	heuristicvalue = 0;
}
}
