



#### Scene-exportoffice

导出word(doc、docx)、批量导出成zip

1. doc：word XML，PC、WPS可以用Office打开，Open Office预览格式是XML
2. docx：修改后缀ZIP，替换部分XML，Office可以预览

PS：docx带有图片，Open Office、手机可以打开文件浏览内容，但图片显示缺失可能是对图片进行了压缩，导致无法预览

[Office在word、excel、PowerPoint2007中保存后图像质量下降](https://docs.microsoft.com/zh-cn/office/troubleshoot/office-suite-issues/office-docuemnt-image-quality-loss)



生成的docx打不开，提示内容错误
可能是图片格式的问题，例如：docx插入图片的格式是jpg，但是生成docx的时候，图片存放的是png格式
调整docx的引入文件格式文件：`[Content_Types].xml`

```xml
<Default ContentType="image/jpeg" Extension="jpg"/>
<Default ContentType="image/png" Extension="png"/>
```





