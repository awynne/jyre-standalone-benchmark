import org.test.Cli;

public class CliTest {

	public static void main(String[] args) throws Exception {
		
		String line = "--zyreImpl java --numResponders 10 --numMsgs 100 --interval 30";

		Cli.main(line.split("\\s+"));

	}

}
