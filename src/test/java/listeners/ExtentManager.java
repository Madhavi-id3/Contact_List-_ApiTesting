package listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    public static ExtentReports extent;

    public static ExtentReports getInstance() {

        if (extent == null) {

            extent = createInstance();

        }

        return extent;
    }

    static String reportName;

    public static ExtentReports createInstance() {

        String timestamp =
                new SimpleDateFormat(
                        "yyyy.MM.dd.HH.mm.ss"
                ).format(new Date());

        reportName =
                "Contact List Application"
                + timestamp
                + ".html";

        String reportPath =
                System.getProperty("user.dir")
                + "/TestReports/"
                + reportName;

        ExtentSparkReporter sparkReporter =
                new ExtentSparkReporter(reportPath);

        sparkReporter.config().setTheme(
                Theme.DARK
        );

        sparkReporter.config().setReportName(
                "Contact List Application Test Report"
        );

        sparkReporter.config().setDocumentTitle(
                "Contact List Application Report"
        );

        ExtentReports extent =
                new ExtentReports();

        extent.attachReporter(
                sparkReporter
        );

        // Additional information
        extent.setSystemInfo(
                "Project",
                "Contact List Application"
        );

        extent.setSystemInfo(
                "Application",
                "Contact List Application"
        );

        extent.setSystemInfo(
                "Framework",
                "Selenium + Rest Assured + TestNG + ExtentReports"
        );

        extent.setSystemInfo(
                "Language",
                "Java"
        );

        return extent;
    }
}