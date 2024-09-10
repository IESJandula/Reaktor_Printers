package es.iesjandula.reaktor_printers_server.utils.resources_handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.iesjandula.reaktor_printers_server.utils.Constants;
import es.iesjandula.reaktor_printers_server.utils.PrintersServerException;

/**
 * @author Francisco Manuel Benítez Chico
 */
public final class ResourcesHandlerFile extends ResourcesHandler
{
	/** Logger of the class */
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesHandlerFile.class);

	/**
	 * @param resourceFolderUrl resource folder URL
	 */
	public ResourcesHandlerFile(URL resourceFolderUrl)
	{
		super(resourceFolderUrl);
	}

	/**
	 * @param destDir destination directory
	 */
	public void copyToDirectory(File destDir) throws PrintersServerException
	{
		File srcDir = new File(getResourceFolderUrl().getFile());
		String absolutePathSrcDir = srcDir.getAbsolutePath();
		this.copyContentToWorkingDirectory(destDir, srcDir, absolutePathSrcDir);
	}

	private void copyContentToWorkingDirectory(File destDir, File srcDir, String absolutePathSrcDir) throws PrintersServerException
	{
		File[] filesList = srcDir.listFiles();
		if (filesList != null)
		{
			for (File file : filesList)
			{
				String subfolderFile = file.getAbsolutePath().replace(absolutePathSrcDir, "");
				this.createFileOrDirectory(destDir, absolutePathSrcDir, file, subfolderFile);
			}
		}
	}

	private void createFileOrDirectory(File destDir, String absolutePathSrcDir, File foundFileDir, String subfolderFile) throws PrintersServerException
	{
		if (foundFileDir.isDirectory())
		{
			this.createDirectoryIfNotExists(destDir, subfolderFile);
			this.copyContentToWorkingDirectory(destDir, foundFileDir, absolutePathSrcDir);
		}
		else
		{
			String filePathWithoutExtraInfo = getFileSubPathWithoutExtraInfoFromFullPath(subfolderFile);
			if (filePathWithoutExtraInfo != null)
			{
				FileInputStream fileInputStream = null;
				try
				{
					fileInputStream = new FileInputStream(foundFileDir);
					this.createFile(destDir, fileInputStream, filePathWithoutExtraInfo);
				}
				catch (IOException ioException)
				{
					String errorString = "IOException while getting an input stream from the file " + foundFileDir.getAbsolutePath();
					LOGGER.error(errorString, ioException);
					
					throw new PrintersServerException(Constants.EXC_ERR_CODE_RESOURCES_HANDLER, errorString, ioException);
				}
				finally
				{
					this.closeStream(fileInputStream);
				}
			}
		}
	}

	protected String getSeparator()
	{
		return File.separator;
	}
}
