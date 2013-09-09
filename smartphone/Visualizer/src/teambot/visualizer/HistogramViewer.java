package teambot.visualizer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import teambot.common.interfaces.IHistoVisualizer;

public class HistogramViewer extends ApplicationFrame implements IHistoVisualizer
{
	protected ChartPanel chartPanel;
	private static final long serialVersionUID = 1L;
	protected float _maxValue;

	public HistogramViewer(String title, float maxValue)
	{
		super(title);
		_maxValue = maxValue; 
	}

	public void update(double[] newValues)
	{
		HistogramDataset dataSet = new HistogramDataset();
		dataSet.setType(HistogramType.RELATIVE_FREQUENCY);
		dataSet.addSeries("H1", newValues, 1000, 0.0, _maxValue);

		JFreeChart chart = ChartFactory.createHistogram("Histogram", "", "", dataSet, PlotOrientation.VERTICAL, false,
				false, false);
		chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);

		pack();

		if (!isVisible())
		{
			setVisible(true);
		}
	}
}
