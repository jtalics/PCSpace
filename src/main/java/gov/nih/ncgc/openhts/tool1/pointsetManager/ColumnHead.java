/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.pointsetManager;

import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class ColumnHead implements Persistent {
  public enum Kind {
    NumberFloat("D"), String("S"), NumberInt("X");
    String abbrev;
    
    Kind(final String abbrev) {
      // unused, but keep for future use
      this.abbrev = abbrev;
    }
    
    public String getAbbrev() {
      return abbrev;
    }
  }

  protected Kind kind;
  
  private String name="<none>";
  private String description="<none>";
  private Session session;

  public ColumnHead(Session session) {
    this.session = session;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setName(final String name) {
    if (Session.isLegalName(name, false)) {
      this.name = name;
    }
    else {
      throw new RuntimeException("illegal name: " + name);
      // this.name = Session.legalizeName(name);
    }
  }

  @Override
	public void initializeTransient() {
    // nop
  }

  @Override
	public void setSession(Session session) {
    this.session = session;
  }

//  public void setLimit(int limitPercentage, boolean showBelowLimit) {
//    this.limitPercentage = limitPercentage;
//    this.showBelowLimit = showBelowLimit;
//  }

  public Stats getStats(Object object) {
    if (object instanceof Pointset) {
      return ((Pointset)object).getStats(this);
    }
    else if (object instanceof Basis) {
      return ((Basis)object).getStats(this);
    }
    else {
      throw new RuntimeException("unknown type");
    }
  }

  public Kind getColumnHeadKind() {
    return kind;
  }

  public void setColumnHeadKind(Kind kind) {
    this.kind = kind;
  }
  
  public static boolean legalKind(String kindString) {
    for (Kind kind : Kind.values()) {
      if (kind.name().equals(kindString)) {
        return true;
      }
    }
    return false;
  }

  public Kind getKind() {
    return kind;
  }
}
