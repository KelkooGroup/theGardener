package utils

import java.io.File
import java.io.File.separator

import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.storage.file.FileBasedConfig
import org.eclipse.jgit.util.{FS, SystemReader}

object CustomConfigSystemReader {

  def overrideSystemGitConfig(): Unit = {
    val userGitConfig = new File(s"target${separator}data${separator}gitconfig")
    SystemReader.setInstance(new CustomConfigSystemReader(userGitConfig))
  }
}

//noinspection ScalaStyle
class CustomConfigSystemReader(userGitConfig: File) extends SystemReader {
  val proxy = SystemReader.getInstance()

  override def getHostname: String = proxy.getHostname
  override def getenv(variable: String): String = proxy.getenv(variable)
  override def getProperty(key: String): String = proxy.getProperty(key)

  override def getCurrentTime: Long = proxy.getCurrentTime
  override def getTimezone(when: Long): Int = proxy.getTimezone(when)

  override def openJGitConfig(parent: Config, fs: FS): FileBasedConfig = proxy.openJGitConfig(parent, fs)
  override def openUserConfig(parent: Config, fs: FS): FileBasedConfig = new FileBasedConfig(parent, userGitConfig, fs)
  override def openSystemConfig(parent: Config, fs: FS): FileBasedConfig = new FileBasedConfig(parent, null, fs) {
    override def load(): Unit = ()
    override def isOutdated: Boolean = false
  }

}
