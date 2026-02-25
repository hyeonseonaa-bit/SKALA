/**
 * 포트폴리오 출력 형식을 정의하는 인터페이스입니다.
 * @FunctionalInterface: 단일 추상 메서드(SAM)만 가진 함수형 인터페이스임을 명시
 * 람다식으로 구현 가능 → PortfolioFormatter formatter = portfolio -> "...";
 * 출력 형식을 유연하게 교체 가능 
 */
@FunctionalInterface
public interface PortfolioFormatter {

// 포트폴리오를 원하는 형식의 문자열로 변환
String format(Portfolio portfolio);
}
