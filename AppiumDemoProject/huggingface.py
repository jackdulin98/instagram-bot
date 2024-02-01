from gradio_client import Client

client = Client("https://vision-cair-minigpt4.hf.space/")
result = client.predict(fn_index=1)
print(result)