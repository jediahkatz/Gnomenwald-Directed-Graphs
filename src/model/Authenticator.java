package model;

//for fun, here's a probability table:
//probability of one request being approved vs. number of gnomes

/*
 * GNOMES:		PROBABILITY (as a percentage):
 * 
 * 1			0%
 * 2			0%
 * 3			2.7%
 * 4			8.37%
 * 5			16.31%
 * 6			25.57%
 * 7			35.29%
 * 8			44.82%
 * 9			53.72%
 * 10			61.72%
 * 11			68.73%
 * 12			74.72%
 */

public class Authenticator {
	private static final double EPSILON = 0.00001;
	
	private Queue<Gnome> gnomes;
	private double answer; //(a + b) * (c - a)
	
	public Authenticator(Model m) {
		this(m.getGnomes());
	}
	
	public Authenticator(Queue<Gnome> gnomes) {
		this.gnomes = gnomes;
		
		//generate a random quadratic
		//y = ax^2 + bx + c
		int a = MyRandom.randInt(-1000, 1000);
		int b = MyRandom.randInt(-1000, 1000);
		int c = MyRandom.randInt(-1000, 1000);
		
		answer = (a + b) * (c - a);
					
		for(Gnome g : gnomes) {
			//give every gnome a random point on the quadratic
			double x = MyRandom.randDouble(-100, 100);
			double y = a*x*x + b*x + c;
			
			g.setSecretPoint(new Point(x,y));
		}
	}
	
	public boolean requestInfo(Gnome gnome) throws SecurityException {
		Queue<Point> points = new Queue<Point>();
		
		//at least 3 gnomes must give permission in order to get a gnome's info
		for(Gnome g : gnomes) {
			Point pt = g.requestSecretPoint();
			if(pt != null)
				points.add(pt);
		}
		
		//copy the linked list into an array
		Point[] ptsArray = new Point[points.size()];
		for(int i=0; i<ptsArray.length; i++) {
			ptsArray[i] = points.dequeue();
		}
		
		boolean authenticated = false;
		//now try all combinations (not permutations)
		for(Point[] combo : combinations3(ptsArray)) {
			if(!authenticated) {
				authenticated = tryPoints(combo[0], combo[1], combo[2]);
			}
		}
		
		if(!authenticated) {
			throw new SecurityException("Sorry, but you were unable to get three gnomes to give permission.");
		}
		
		return true;
	}
	
	private boolean tryPoints(Point p1, Point p2, Point p3) {
		//calculate a, b, and c through Lagrange polynomial interpolation
		double x1 = p1.getX(), x2 = p2.getX(), x3 = p3.getX(), y1 = p1.getY(), y2 = p2.getY(), y3 = p3.getY();
	
		double a = y1/((x1-x2)*(x1-x3)) + y2/((x2-x1)*(x2-x3)) + y3/((x3-x1)*(x3-x2));
		double b = -y1*(x2+x3)/((x1-x2)*(x1-x3))-y2*(x1+x3)/((x2-x1)*(x2-x3))-y3*(x1+x2)/((x3-x1)*(x3-x2));
		double c = y1*x2*x3/((x1-x2)*(x1-x3)) + y2*x1*x3/((x2-x1)*(x2-x3)) + y3*x1*x2/((x3-x1)*(x3-x2));
						
		double ans = (a + b)*(c - a);
				
		return (Math.abs(answer - ans) < EPSILON);
	}
	
	//get all the combinations (NOT PERMUTATIONS!) of 3 points in the given array
    private Queue<Point[]> combinations3(Point[] arr){
    	Queue<Point[]> list = new Queue<Point[]>();
        for(int i = 0; i<arr.length-2; i++)
            for(int j = i+1; j<arr.length-1; j++)
                for(int k = j+1; k<arr.length; k++)
                    list.add(new Point[]{arr[i],arr[j],arr[k]});
        return list;
    }
}
