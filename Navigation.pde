/*
 * This program is incomplete.  Currently, it is an editor for
 * defining polygons.  Eventually, I intended to create a system
 * for finding routes between points via movement between Polys.
 */

ArrayList polys;
ArrayList points;

final int R=2; // point radius
final int D=20; // selection radius

ArrayList tools;
int currentTool;

void setup() {
  size(500,500);
  polys = new ArrayList();
  points = new ArrayList();
  tools = new ArrayList();
  tools.add(new PointTool());
  tools.add(new PolyTool());
  currentTool = 0;
}

Tool tool() {
  return (Tool)tools.get(currentTool);
}

void draw() {
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

void keyTyped() {
  if(key=='t') {
    currentTool = (currentTool == tools.size()-1 ? 0 : currentTool + 1);
  }
}

void mouseClicked() {
  tool().click();
}
