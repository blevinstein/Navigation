import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Navigation extends PApplet {

ArrayList polys;
ArrayList points;

final int R=2; // point radius
final int D=20; // selection radius

ArrayList tools;
int currentTool;

public void setup() {
  size(500,500);
  polys = new ArrayList();
  points = new ArrayList();
  tools = new ArrayList();
  tools.add(new PointTool());
  tools.add(new PolyTool());
  currentTool = 0;
}

public Tool tool() {
  return (Tool)tools.get(currentTool);
}

public void draw() {
  noStroke();
  // fill bg
  fill(255);
  rect(0,0,width,height);
  // tool name
  fill(0);
  text(tool().name(),0,textAscent());
  // points
  for(int c=0;c<points.size();c++) {
    drawPoint(c);
  }
  // polys
  for(int c=0;c<polys.size();c++) {
    drawPoly(ply(c).points,state(ply(c)));
  }
  tool().draw();
}

public void keyTyped() {
  if(key=='t') {
    currentTool = (currentTool == tools.size()-1 ? 0 : currentTool + 1);
  }
}

public void mouseClicked() {
  tool().click();
}
public void drawPoint(Object o) {
  Point p = pt(o);
  noStroke();
  fill(0);
  ellipse(p.x,p.y,2*R,2*R);
}

public void circlePoint(Object o) {
  Point p = pt(o);
  stroke(0);
  noFill();
  ellipse(p.x,p.y,2*D,2*D);
}

public void drawEdge(Object a,Object b) {
  Point pa = pt(a);
  Point pb = pt(b);
  stroke(0);
  noFill();
  line(pa.x,pa.y,pb.x,pb.y);
}

public void drawPoly(ArrayList poly) {
  drawPoly(poly,0);
}
public void drawPoly(ArrayList poly,int state) {
  switch(state) {
    case 1:
      stroke(255,0,0,100);
      fill(255,0,0,50);
      break;
    case 0:
    default:
      stroke(0,0,255,100);
      fill(0,0,255,50);
      break;
  }
  beginShape();
  for(int c=0;c<poly.size();c++) {
    Point p = pt(poly.get(c));
    vertex(p.x,p.y);
  }
  endShape(CLOSE);
}

public void drawLines(ArrayList poly) {
  stroke(0);
  noFill();
  beginShape();
  for(int c=0;c<poly.size();c++) {
    Point p = pt(poly.get(c));
    vertex(p.x,p.y);
  }
  endShape();
}
class Poly {
  ArrayList points; // int id
  ArrayList edges; // int id
  Poly(ArrayList p) {
    points = p;
    edges = new ArrayList();
  }
  /*
   * Detects whether or not a point is contained based on the scanline method
   */
  public boolean contains(Object o) {
    Point p = pt(o);
    int P = this.points.size();
    boolean ret = false;
    for(int c=0;c<P-1;c++) {
      Point a1 = pt(this.points.get(c));
      Point a2 = pt(this.points.get((c+1)%P));
      Point at = a2.sub(a1);
      float miny = min(a1.y,a2.y);
      float maxy = max(a1.y,a2.y);
      float projx = (at.y==0 ? a1.x : a1.x + at.x/at.y*(p.y-a1.y));
      if(p.y >= miny && p.y < maxy && p.x >= projx)
        ret = !ret;
    }
    return ret;
  }
}

class Point {
  float x;
  float y;
  Point(float x,float y) {
    this.x = x;
    this.y = y;
  }
  public Point sub(Point p) {
    return new Point(this.x-p.x,this.y-p.y);
  }
  public float norm2() {
    return sq(this.x)+sq(this.y);
  }
  public float norm() {
    return sqrt(norm2());
  }
}

public boolean intersect(Object a1,Object a2,Object b1,Object b2) {
  Point a0 = pt(a1);
  Point at = pt(a2).sub(pt(a1));
  Point b0 = pt(b1);
  Point bt = pt(b2).sub(pt(b1));
  float d = at.y*bt.x-at.x*bt.y;
  float u = (bt.x*(b0.y-a0.y)-bt.y*(b0.x-a0.x))/d;
  float v = (at.x*(b0.y-a0.y)-at.y*(b0.x-a0.x))/d;
  return (u >= 0 && u <= 1 && v>= 0 && v<= 1);
}

// detects self-intersecting edges
public int state(Object o) {
  Poly p = ply(o);
  int P = p.points.size();
  for(int c=0;c<P;c++)
    for(int d=0;d<P;d++) {
      if(abs(c-d)<=1 || (c==0 && d==P-1) || (d==0 && c==P-1)) continue;
      if(intersect(p.points.get(c),p.points.get((c+1)%P),p.points.get(d),p.points.get((d+1)%P)))
        return 1;
    }
  return 0;
}
interface Tool {
  public String name();
  public void click();
  public void draw();
}

class LinkTool implements Tool {
  public String name() {
    return "Link";
  }
  public void click() {
  }
  public void draw() {
  }
}

class PointTool implements Tool {
  public String name() {
    return "Point";
  }
  public void click() {
    points.add(mousePoint());
  }
  public void draw() {
    drawPoint(mousePoint());
  }
}

class PolyTool implements Tool {
  ArrayList poly;
  PolyTool() {
    poly = new ArrayList();
  }
  public String name() {
    return "Poly";
  }
  public int last() {
    if(poly.size()>0) {
      return (Integer)poly.get(poly.size()-1);
    }
    return -1;
  }
  public void click() {
    int n=grabPoint();
    if (n == -1) return;
    
    int e=-1;
    for(int c=0;c<poly.size();c++)
      if((Integer)poly.get(c)==n)
        e=c;

    if(e > 0) {
      return; // not allowed to self-intersect
    } else if(e == 0) { // close poly
      polys.add(new Poly(poly));
      poly = new ArrayList();
    } else {
      poly.add(n);
    }
  }
  public void draw() {
    int n,s;
    drawLines(poly);
    if((s=last()) != -1) drawEdge(s,mousePoint());
    if((n=grabPoint()) != -1) circlePoint(n);
  }
}

public Point mousePoint() {
  return new Point(mouseX,mouseY);
}

public int grabPoint() {
  float d=D;
  int r=-1;
  float t;
  Point m = mousePoint();
  for(int c=0;c<points.size();c++) {
    Point p = (Point)points.get(c);
    if(p==null) continue;
    if((t=m.sub(p).norm()) < d) {
      d=t;
      r=c;
    }
  }
  return r;
}

public Point pt(Object o) {
  if(o instanceof Integer)
    return (Point)points.get((Integer)o);
  else
    return (Point)o;
}

public Poly ply(Object o) {
  if(o instanceof Integer)
    return (Poly)polys.get((Integer)o);
  else
    return (Poly)o;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Navigation" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
