// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.packer;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestMain {

  /*
   * HINT: Wenn dieser Test fehlschlägt, dann weil die class Main umbenannt oder
   * verschoben wurde Dann unbedingt auch die pom.xml anpassen
   * (maven-assembly-plugin artifact plugin)
   */
  private Main mainSUT;

  @Before
  public void setUp() {
    mainSUT = new Main();
  }

  @Test
  public void test() throws IOException {
    Assert.assertEquals("lla.privat.atarixl.packer.Main", mainSUT.getClass().getCanonicalName());
  }

  @Test
  public void testPwd() {
    System.out.println(getPwd().getAbsolutePath());
  }

  private File getPwd() {
    final File aFile = new File("target/test-classes", this.getClass().getName().replace('.', '/') + ".class");
    return aFile.getParentFile();
  }

}
