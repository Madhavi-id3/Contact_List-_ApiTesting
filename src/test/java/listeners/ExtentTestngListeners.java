package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class ExtentTestngListeners
        implements ITestListener {

    ExtentReports extent =
            ExtentManager.getInstance();

    ThreadLocal<ExtentTest> parentTest =
            new ThreadLocal<ExtentTest>();


    @Override
    public void onTestStart(
            ITestResult result) {

        ExtentTest extentTest =
                extent.createTest(
                        result.getMethod()
                              .getMethodName()
                );

        parentTest.set(extentTest);

        parentTest.get().log(
                Status.INFO,
                "Test Started"
        );
    }


    @Override
    public void onTestSuccess(
            ITestResult result) {

        parentTest.get().log(
                Status.PASS,
                result.getName()
                + " - Test Passed Successfully"
        );
    }


    @Override
    public void onTestFailure(
            ITestResult result) {

        parentTest.get().log(
                Status.FAIL,
                result.getName()
                + " - Test Failed"
        );

        if (result.getThrowable() != null) {

            parentTest.get().fail(
                    result.getThrowable()
            );
        }

        System.out.println(
                "Test Failure: "
                + result.getMethod()
                      .getMethodName()
        );
    }


    @Override
    public void onTestSkipped(
            ITestResult result) {

        parentTest.get().log(
                Status.SKIP,
                result.getName()
                + " - Test Skipped"
        );

        if (result.getThrowable() != null) {

            parentTest.get().skip(
                    result.getThrowable()
            );
        }
    }


    @Override
    public void onFinish(
            ITestContext context) {

        extent.flush();

        System.out.println(
                "Extent Report generated successfully."
        );
    }
}