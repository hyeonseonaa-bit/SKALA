import java.util.List;

/**
 * 포트폴리오를 파일 저장 형식의 문자열로 변환합니다.
 */
public class FilePortfolioFormatter implements PortfolioFormatter {

    /**
     * 포트폴리오의 주식 목록을 번호가 매겨진 형식의 문자열로 반환
     */
    @Override
    public String format(Portfolio portfolio) {
        List<Stock> stocks = portfolio.getStocksAsList();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stocks.size(); i++) {
            sb.append(i + 1)
              .append(". ")
              .append(stocks.get(i).toString())
              .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
