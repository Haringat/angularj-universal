package ch.swaechter.springular.renderer.assets;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/**
 * The class FilesystemProvider represents an asset provider that is using the assets from a file system. If the files
 * change, the asset provider will reload them and request the render engine to reload itself with the new assets. As a
 * reason of that, this provider enables live reload during development.
 *
 * @author Simon Wächter
 */
public class FilesystemProvider implements RenderAssetProvider {

    /**
     * File that is used to get the content of the index template.
     */
    private final File indexfile;

    /**
     * File that is used to get the server bundle.
     */
    private final File serverbundlefile;

    /**
     * Charset that is used to read the files.
     */
    private final Charset charset;

    /**
     * Create a new file system provider and listen to file changes.
     *
     * @param indexfile        File that will be used to get the content of the index template
     * @param serverbundlefile File that will be used to get the server bundle
     * @param charset          Charset that will be used to read the files
     */
    public FilesystemProvider(File indexfile, File serverbundlefile, Charset charset) {
        this.indexfile = indexfile;
        this.serverbundlefile = serverbundlefile;
        this.charset = charset;
        try {
            new FilesystemWatcher(indexfile, serverbundlefile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get the content of the index template.
     *
     * @return Content of the index template.
     * @throws IOException Exception in case of an IO problem
     */
    @Override
    public String getIndexContent() throws IOException {
        InputStream inputstream = new FileInputStream(indexfile);
        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
        int result = inputstream.read();
        while (result != -1) {
            outputstream.write((byte) result);
            result = inputstream.read();
        }
        return outputstream.toString(charset.name());
    }

    /**
     * Get the file of the server bundle.
     *
     * @return File of the server bundle
     * @throws IOException Exception in case of an IO problem
     */
    @Override
    public File getServerBundle() throws IOException {
        return serverbundlefile;
    }

    private class FilesystemWatcher {

        private final WatchService watchservice;

        public FilesystemWatcher(File indexfile, File serverbundlefile) throws IOException {
            this.watchservice = FileSystems.getDefault().newWatchService();
            Path indexpath = Paths.get(indexfile.getParent());
            Path serverbundlepath = Paths.get(serverbundlefile.getParent());
            indexpath.register(watchservice, StandardWatchEventKinds.ENTRY_MODIFY);
            serverbundlepath.register(watchservice, StandardWatchEventKinds.ENTRY_MODIFY);
            try {
                while (true) {
                    WatchKey watchkey = watchservice.poll(10, TimeUnit.MINUTES);
                    if (watchkey != null) {
                        watchkey.pollEvents().forEach(event -> System.out.println(event.context()));
                    }
                    watchkey.reset();
                }
            } catch (InterruptedException exception) {

            }
        }
    }

    /**
     * Check if the provider does support live reload.
     *
     * @return Status if the provider supports live reload
     */
    @Override
    public boolean isLiveReloadSupported() {
        return false;
    }

    /**
     * Check if the provider detected an asset chance and wishes a live reload.
     *
     * @return Status of the provider wishes a live reload
     */
    @Override
    public boolean isLiveReloadRequired() {
        return false;
    }
}