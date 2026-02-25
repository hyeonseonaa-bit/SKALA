/**
 * 주식의 이름, 가격, 수량을 저장하는 데이터 클래스입니다.
 */
public class Stock {

    // private: 클래스 외부에서 직접 접근 불가 (캡슐화)
    // Getter/Setter를 통해서만 접근하도록 강제함
    private String name;     
    private int price;       
    private int quantity;  

    // 모든 속성을 초기화하는 생성자
    // this.필드명 은 클래스의 필드를 가리킴

    public Stock(String name, int price, int quantity) {
        this.name = name;         
        this.price = price;       
        this.quantity = quantity; 
    }

    // Getters: private 필드의 값을 읽을 수 있도록 제공하는 메서드
    // 외부에서 stock.name 처럼 직접 접근하는 대신 stock.getName() 으로 접근
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }

    // Setters: private 필드의 값을 변경할 수 있도록 제공하는 메서드
    public void setName(String name) { this.name = name; }
    public void setPrice(int price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    
    // 객체를 문자열로 표현하는 메서드 (Object 클래스에서 상속, @Override로 재정의)
    // System.out.println(stock) 호출 시 자동으로 이 메서드가 실행됨
    @Override
    public String toString() {
        return "종목: " + name + ", 현재가: " + price + ", 보유수량: " + quantity;
    }
}
