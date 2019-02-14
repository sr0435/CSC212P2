package edu.smith.cs.csc212.p2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Bubble extends WorldObject{

	public Bubble(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	private int dt = 0;
	
	public void draw(Graphics2D g) {
		dt += 1;
		if (dt > 100) {
			dt = 0;
		}
		
		Shape circle = new Ellipse2D.Double(-0.6, -0.6, 1.2, 1.2);
		
		Graphics2D flipped = (Graphics2D) g.create();
		if (dt < 50) {
			flipped.scale(-1, 1);
		}
			flipped.setColor(new Color(1f,1f,1f,0.5f));
			flipped.fill(circle);
}

	@Override
	public void step() {
		
		
	}}
