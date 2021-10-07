package gov.nih.ncgc.openhts.tool1.demo;

import gov.nih.ncgc.openhts.tool1.Session;

public abstract class Demo {
  protected final String name;
  protected final Session session;
  protected final DemoRobot r;

  public Demo(final Session session, final DemoRobot robot, final String name) {
    this.session = session;
    this.r = robot;
    this.name = name;
  }

  public abstract void perform() throws Exception;

  public String getName() {
    return name;
  }
}
