# 1. 使用 Python 官方镜像
FROM python:3.10-slim

# 2. 设置工作目录
WORKDIR /app

# 3. 拷贝文件
COPY flask_server/ /app

# 4. 安装依赖
RUN pip install --no-cache-dir -r requirements.txt

# 5. 暴露端口（如果需要）
EXPOSE 5002

# 加入测试命令（可选）
CMD ["sh", "-c", "python app.py & sleep 5 && python test.py"]