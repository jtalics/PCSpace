package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;


public class TokenCollector implements TokenManager {

  @Override
	public Token getNextToken() {
    return ProducerConsumer.pc.getToken();
  }

}
