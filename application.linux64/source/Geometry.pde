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
  boolean contains(Object o) {
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
  Point sub(Point p) {
    return new Point(this.x-p.x,this.y-p.y);
  }
  float norm2() {
    return sq(this.x)+sq(this.y);
  }
  float norm() {
    return sqrt(norm2());
  }
}

boolean intersect(Object a1,Object a2,Object b1,Object b2) {
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
int state(Object o) {
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
