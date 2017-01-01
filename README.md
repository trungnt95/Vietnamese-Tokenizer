NLP Word Segmentation

Đây là công cụ tách từ được xây dựng bởi Nguyễn Thành Trung và Đỗ Việt Vương.
Đồng thời, công cụ này là bài tập lớn môn Xử lí ngôn ngữ tự nhiên - Giảng viên Nguyễn Phương Thái

Ý niệm chính của phương pháp tách từ được sử dụng trong công cụ là so khớp cực đại (longest matching word)
dựa trên từ điển (sử dụng Treebanks VLSP và một phần bộ dữ liệu huấn luyện gồm các văn bản đã được tách từ).

Các bước thực hiện:
	1.	Sử dụng các regular expressions để nhận diện email, url, phrase, unit, name... 
		Xử lí với unit, name để phân tách từ rõ ràng hơn 
		(tuy nhiên chưa xử lí được với trường hợp tên riêng viết hoa hoàn toàn, ví dụ: LAM ĐIỀN)
	2.	Đối với các phrase thì dựa vào từ điển đã xây dựng ở trên để tách tiến hành tách từ trong từng phrase.

NOTE: Các câu trong một tệp thì phải được phân cách, mỗi câu cần được nằm trên một dòng riêng biệt.