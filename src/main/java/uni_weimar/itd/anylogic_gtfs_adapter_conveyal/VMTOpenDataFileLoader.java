package uni_weimar.itd.anylogic_gtfs_adapter_conveyal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.conveyal.gtfs.GTFSFeed;

public class VMTOpenDataFileLoader {

	private GTFSFeed reader;
	private String gtfsFilePath;
	private boolean temporaryFile;
	
	/**
	 * default constructor
	 * @param dataLocation
	 */
	public VMTOpenDataFileLoader()
	{
		this.temporaryFile = false;
	}
	
	/**
	 * Load a online gtfs file
	 * @param fileUrl
	 */
	public void loadFileFromUrl (String fileUrl)
	{
		String tempPath = "myLocalGtfs.zip";
		this.downloadFile(fileUrl, tempPath);
		this.temporaryFile = true;
		this.loadLocalFile(tempPath);
	}
	
	/**
	 * load a local gtfs file
	 * @param localPath
	 */
	public void loadLocalFile (String localPath)
	{
		//open File
		this.gtfsFilePath = localPath;
		
		//prepare Reader
		this.reader = GTFSFeed.fromFile(this.gtfsFilePath);
	}
	
	/**
	 * Download an online file
	 * @param sourceUrl
	 * @param pathToSave
	 */
	private void downloadFile(String sourceUrl, String pathToSave)
	{
		URL dataUrl;
		try {
			//Overwrite file
			File fileToSave = new File(pathToSave);
			if (fileToSave.exists())
			{
				fileToSave.delete();
			}
			
			//Open Streams
			dataUrl = new URL(sourceUrl);
			InputStream urlInputStream = dataUrl.openStream();
			FileOutputStream writeStream = new FileOutputStream(pathToSave);
			
			//Read data
			int bufferSize = 65535;
			byte dataBuffer[] = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = urlInputStream.read(dataBuffer, 0, bufferSize)) != -1)
			{
				writeStream.write(dataBuffer, 0, bytesRead);
			}
			
			//close streams
			writeStream.close();
			urlInputStream.close();
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * get the data feed
	 * @return
	 */
	public GTFSFeed getDataFeed()
	{
		return this.reader;
	}

	/**
	 * closes the data reader and related file streams
	 */
	public void close()
	{
		if (this.reader != null)
		{
			this.reader.close();
		}
		
		if (this.temporaryFile)
		{
			File fileToDelete = new File(this.gtfsFilePath);
			fileToDelete.delete();
		}
	}
}
