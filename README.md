# m

این یک **فورک شخصی** از [v2rayNG](https://github.com/2dust/v2rayNG) (کلاینت رسمی و متن‌باز V2Ray/Xray برای اندروید) است که توسط [mosikh98](https://github.com/mosikh98) برای استفاده‌ی شخصی ویرایش شده.

> ⚠️ این نسخه رسمی نیست و ارتباطی با تیم اصلی v2rayNG ندارد. فقط برای استفاده‌ی شخصی ساخته شده.

## تغییرات نسبت به نسخه‌ی اصلی

- **برندینگ:** نام برنامه و آیکون با یک لوگوی ساده‌ی «M» جایگزین شد
- **تم رنگی:** یک پالت ۱۲ رنگی در تنظیمات اضافه شد که رنگ اصلی برنامه (دکمه‌ها، هایلایت‌ها) رو تغییر می‌ده
## بیلد از سورس

این پروژه با GitHub Actions به‌صورت خودکار بیلد می‌شود (`.github/workflows/build.yml`). برای بیلد دستی با Android Studio یا از خط فرمان:

```bash
cd V2rayNG
./gradlew assembleDebug
```

APK خروجی در مسیر `V2rayNG/app/build/outputs/apk/` قرار می‌گیرد. بیلد نیاز به Android NDK (برای کتابخونه‌ی تونل native) و دانلود `libv2ray.aar` از [2dust/AndroidLibXrayLite](https://github.com/2dust/AndroidLibXrayLite) دارد — این مراحل در workflow خودکار انجام می‌شوند.

## منبع اصلی

برای مستندات کامل، لیست پروتکل‌های پشتیبانی‌شده، و اطلاعات بیشتر به ریپوی اصلی مراجعه کنید:
[github.com/2dust/v2rayNG](https://github.com/2dust/v2rayNG)
