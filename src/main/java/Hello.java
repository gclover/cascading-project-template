import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.FlowDef;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.pipe.Pipe;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;

public class Hello {

  public static void main(String[] args) {

    String input = args[0];
    String output = args[1];


    Tap inTap = new Hfs(new TextLine(), input);
    Tap outTap = new Hfs(new TextLine(), output);

    Pipe pipe = new Pipe("hello pipe");

    FlowDef flowDef = new FlowDef()
        .setName("hello flow")
        .addSource(pipe, inTap)
        .addTailSink(pipe, outTap);

    FlowConnector connector = new HadoopFlowConnector();
    Flow flow = connector.connect(flowDef);
    flow.complete();

  }
}
