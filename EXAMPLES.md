# Contoh Extension

Setiap extension butuh **satu file script** (`.js` atau `.css`) dan **satu manifest** (diisi lewat form, tidak perlu file terpisah).

## 1. Dark Mode Paksa (CSS)
- Tipe: `css`
- Match: `*://*/*`
- Run at: `document_start`
- Kode:
```css
html { filter: invert(1) hue-rotate(180deg) !important; }
img, video, picture { filter: invert(1) hue-rotate(180deg) !important; }
```

## 2. Auto-fill Username (JS)
- Tipe: `js`
- Match: `*.example.com/*`
- Run at: `document_end`
- Kode:
```js
var u = document.querySelector('input[name="username"]');
if (u) u.value = 'userku';
```

## 3. Ad-block Sederhana (JS)
- Tipe: `js`
- Match: `*://*/*`
- Run at: `document_start`
- Kode:
```js
var blocked = ['ads','banner','popunder'];
document.addEventListener('DOMNodeInserted', function(e){
  var el = e.target;
  if (el.nodeType === 1) {
    var s = (el.id||'') + (el.className||'');
    if (blocked.some(function(b){ return s.toLowerCase().indexOf(b) >= 0; })) {
      el.style.display = 'none';
    }
  }
});
```

## 4. Ubah Tampilan Situs (JS)
- Tipe: `js`
- Match: `*.tokopedia.com/*`
- Run at: `document_end`
- Kode:
```js
document.body.style.fontFamily = 'Comic Sans MS, cursive';
```
