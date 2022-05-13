import pymysql

db = pymysql.connect(
    host="localhost",
    port=3306,
    user='root',
    password='123',
    db='mysql',
    charset='utf8'
)

cursor=db.cursor()
cursor.execute('select version()')

data=cursor.fetchone()

print("database connect success,version: %s"%data)

db.close()