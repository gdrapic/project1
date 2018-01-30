


@ComponentScan
@EnableAsync
public class MainJob implements ApplicationContextAware, DisposableBean{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainJob.class);

	
	ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context=context;
	}

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(MainJob.class);

		
		AbstractJob job = (AbstractJob) context.getBean(AppConstants.JOB_KEY);// new JobImpl();


//		String[] beanNames = context.getBeanDefinitionNames();
//		for (String string : beanNames) {
//			System.out.println(string);
//		}
	}
	

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
