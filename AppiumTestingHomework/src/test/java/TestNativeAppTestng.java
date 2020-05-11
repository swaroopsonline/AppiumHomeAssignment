import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

public class TestNativeAppTestng {

	public static AndroidDriver driver;
	
	/* The AppiumDriverLocalService is invoked by nodeJS executable and the appium. The path of the files
	 * indicate the location of the respective executables of Appium and nodejs.
	 * This is done, so that the Appium need not be started separately.
	 * Also the path of the log.txt is given, where the logs could be checked, once the Framework is run.
	 * The local Time Zone could be shown in the log file with the help of the GeneralServerFlag enum */

	AppiumDriverLocalService service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
			.usingDriverExecutable(new File("C:\\Program Files\\nodejs\\node.exe"))
			.withAppiumJS(
					new File("C:\\Users\\swaro\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
			.withLogFile(new File(System.getProperty("user.dir") + "\\src\\test\\resources\\logs\\log.txt"))
			.withArgument(GeneralServerFlag.LOCAL_TIMEZONE));
	
	/*The reference object of the DesiredCapabilities Class will be needed in the Testcases. 
	 *And so the DesiredCapabilities class is instantiated. */
	DesiredCapabilities capabilities = new DesiredCapabilities();
	
	
	/*The Appium service is started before the Testcases are executed by the service.start() */
	@BeforeSuite
	public void startAppium() {

		service.start();
	}
	
	
	/* The invokeNativeApp() will be used to invoke the Android app of Seek.com,
	 * with the help of the Java package of the Android app. Also the Activity name of the Android
	 * activity is given, in order to exercise the functionality of login to the App. */
	@Test
	public void invokeNativeApp() throws InterruptedException, MalformedURLException {
		
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android");
		capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "au.com.seek");

		capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "au.com.seek.ui.mainview.MainActivity");

		Thread.sleep(4000); /* The Interrupted exception could be avoided. However, we've given the
		Thread.sleep, so that we'll be able to see the activities done on the real device */ 
		
		/* The driver is used to specify the connection details for the Appium Server,
		 * which is invoked using the object reference of the DesiredCapabilities class */
		driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

		driver.manage().timeouts().implicitlyWait(10L, TimeUnit.SECONDS);
		
		/*The below 5 lines of code is used to confirm, if we're indeed on the Sign In page.
		 *The assertion of TestNG is used, which is used to pass or fail the test. */
		String SignInText = driver.findElement(By.id("au.com.seek:id/btn_submit")).getText();
		String ExpectedText = "Sign in";
		
		/*The below assertion is given, so that we'd be able to confirm, if indeed we're on
		 *the Login screen. The SignIn text is verified on the SignIn button of the Seek app*/
		Assert.assertEquals(SignInText, ExpectedText);
		System.out.println("The text on the Login screen is verified");
		System.out.println("The Test Case is passed");
		
	}
	
	
	/*The below method is used to exercise the functionality of login to the Seek android app.
	 * The execution of the Test case is dependent on the preceding method of 'invokeNativeApp',
	 * wherein it is skipped if the preceding method fails*/
	@Test(dependsOnMethods = { "invokeNativeApp" })
	public void doLogin() throws MalformedURLException, InterruptedException {
		
		// Please give your email address as login in the sendkeys below//
		driver.findElement(By.id("au.com.seek:id/input_email")).sendKeys("youremail@gmail.com");
		
		// Please give your password in the sendkeys below//
		driver.findElement(By.id("au.com.seek:id/input_password")).sendKeys("YourPassword");
		
		driver.findElement(By.id("au.com.seek:id/btn_submit")).click();

		Thread.sleep(4000);/* The Interrupted exception could be avoided. However, we've given the
		Thread.sleep, so that we'll be able to see the activities done on the real device */ 
		
		
		/*The below 5 lines of code is used to confirm, if we've been able to login successfully 
		 *to the Seek android app. 
		 *And as per the requirement, the Testcase, will be considered pass, if we're able to 
		 *progress to the next screen, after the login screen.*/
		String ActualTitle = driver.findElement(By.id("au.com.seek:id/screen_title")).getText();
		String ExpectedTitle = "Find your dream job faster";
		
		/*The below assertion is used to verify, the title of the next screen, once the login
		 *is done successfully.*/		
		Assert.assertEquals(ActualTitle, ExpectedTitle);
		System.out.println("The title on the next screen after the login screen is verified");
		System.out.println("The Test Case is passed");

	}
	
	/*The stopAppium method is used to stop the Appium server, once the Testcases are executed.*/
	@AfterSuite
	public void stopAppium() {
		// driver.quit();
		service.stop();

	}

}
