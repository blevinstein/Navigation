interface Tool {
  String name();
  void click();
  void draw();
}

class LinkTool implements Tool {
  String name() {
    return "Link";
  }
  void click() {
  }
  void draw() {
  }
}

class PointTool implements Tool {
  String name() {
    return "Point";
  }
  void click() {
    points.add(mousePoint());
  }
  void draw() {
    drawPoint(mousePoint());
  }
}

class PolyTool implements Tool {
  ArrayList poly;
  PolyTool() {
    poly = new ArrayList();
  }
  String name() {
    return "Poly";
  }
  int last() {
    if(poly.size()>0) {
      return (Integer)poly.get(poly.size()-1);
    }
    return -1;
  }
  void click() {
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
  void draw() {
    int n,s;
    drawLines(poly);
    if((s=last()) != -1) drawEdge(s,mousePoint());
    if((n=grabPoint()) != -1) circlePoint(n);
  }
}
