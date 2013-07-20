
Point mousePoint() {
  return new Point(mouseX,mouseY);
}

int grabPoint() {
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

Point pt(Object o) {
  if(o instanceof Integer)
    return (Point)points.get((Integer)o);
  else
    return (Point)o;
}

Poly ply(Object o) {
  if(o instanceof Integer)
    return (Poly)polys.get((Integer)o);
  else
    return (Poly)o;
}
