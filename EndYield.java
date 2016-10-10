
public class EndYield {
int direction; // 1 2 3 4, like a clock
int x;
int y;
EndYield(int d, int x1, int y1){
	direction = d;
	x = x1;
	y = y1;
}
boolean equals(EndYield ey){
	return ey.x == x && ey.y == y && ey.direction == direction;
}
}
