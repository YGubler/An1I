package ch.hsr.util;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class Graph extends JPanel {

	double xMin = 0;
	double xMax = 0;
	double yMin = 0;
	double yMax = 0;
	
	private static class Point{
		Color col;
		double xArg;
		double yArg;
		
		public Point(Color c, double x, double y){
			col = c;
			xArg = x;
			yArg = y;
		}
	}

	List<Point> list;
	
	public Graph(){
		super();
		list = new LinkedList<Point>();
	}
	
	public void clear(){
		list = new LinkedList<Point>();
		this.getGraphics().clearRect(0,0,this.getWidth(),this.getHeight());
	}
	
	public void addPoint(Color col, double xArg, double yArg){
		list.add(new Point(col,xArg,yArg));
	}
	
	int calcX(double xArg){
		return 5+(int)((this.getWidth()-5) * (xArg - xMin ) / (xMax - xMin));
	}

	int calcY(double yArg){
		return this.getHeight() -5 - (int)((this.getHeight()-5) * (yArg - yMin ) / (yMax - yMin));
	}

	public void show(){
		repaint();
	}
	
	public void paint(Graphics graphics)
	{
	    super.paint(graphics);
		xMin = 0;
		xMax = 0;
		yMin = 0;
		yMax = 0;
		if (graphics == null) return;
		graphics.clearRect(0,0,this.getWidth(),this.getHeight());

		for(Point point : list){
			if (xMin > point.xArg) xMin = point.xArg;
			if (xMax < point.xArg) xMax = point.xArg;
			if (yMin > point.yArg && -1000 < point.yArg) yMin = point.yArg;
			if (yMax < point.yArg && 1000 > point.yArg) yMax = point.yArg;
		}
		for(Point point : list){
			int x = calcX(point.xArg);
			int y = calcY(point.yArg);
			graphics.setColor(point.col);
			if (point.col == Color.red) {
				graphics.drawOval(x - 3, y - 3, 6, 6);
			} else {
				graphics.drawOval(x, y, 0,0);			
			}	
		}
		
		graphics.setColor(Color.BLACK);
		graphics.drawLine(calcX(0), calcY(yMin), calcX(0), calcY(yMax));
		graphics.drawLine(calcX(xMin), calcY(0), calcX(xMax), calcY(0));
		
		graphics.drawString(String.format("%.2f", xMin), calcX(xMin), calcY(0));
		graphics.drawString(String.format("%.2f", xMax), calcX(xMax)-30, calcY(0));
		graphics.drawString(String.format("%.2f", yMin), calcX(0), calcY(yMin));
		graphics.drawString(String.format("%.2f", yMax), calcX(0), calcY(yMax)+20);
	}
}
