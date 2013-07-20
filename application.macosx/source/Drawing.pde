void drawPoint(Object o) {
  Point p = pt(o);
  noStroke();
  fill(0);
  ellipse(p.x,p.y,2*R,2*R);
}

void circlePoint(Object o) {
  Point p = pt(o);
  stroke(0);
  noFill();
  ellipse(p.x,p.y,2*D,2*D);
}

void drawEdge(Object a,Object b) {
  Point pa = pt(a);
  Point pb = pt(b);
  stroke(0);
  noFill();
  line(pa.x,pa.y,pb.x,pb.y);
}

void drawPoly(ArrayList poly) {
  drawPoly(poly,0);
}
void drawPoly(ArrayList poly,int state) {
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

void drawLines(ArrayList poly) {
  stroke(0);
  noFill();
  beginShape();
  for(int c=0;c<poly.size();c++) {
    Point p = pt(poly.get(c));
    vertex(p.x,p.y);
  }
  endShape();
}
