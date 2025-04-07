package basics.klee2.hierarchical.context;

public class UtilsService {
	
	CommonService commonService;

	public UtilsService(CommonService commonService) {
		this.commonService = commonService;
	}
	
	public void helloU() {
		System.out.println("ici bean UtilsService");
		commonService.hello();
	}
	
	private String prefix;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
