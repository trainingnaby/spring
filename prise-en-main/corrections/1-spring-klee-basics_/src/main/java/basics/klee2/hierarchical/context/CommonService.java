package basics.klee2.hierarchical.context;

public class CommonService {
	
	public void hello() {
		System.out.println("Hello from CommonService bean");
	}
	private String prefix;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
