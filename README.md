# MaskEditText

## How to use
Simply add this code to the layout file, specify input type, mask. Component behaves as usual EditText

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.gbksoft.mask_et.MaskEditText
    android:id="@+id/etMask"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="12dp"
    android:layout_marginBottom="12dp"
    app:show_mask_as_hint="true"
    app:mask="+38 (0##) ###-##-##" />
```

Set custom mask char representation (default - #)
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.gbksoft.mask_et.MaskEditText
    android:id="@+id/etMask"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="12dp"
    android:layout_marginBottom="12dp"
    app:show_mask_as_hint="true"
    app:char_representation="m"
    app:mask="+38 (0mm) mmm-mm-mm" />
```

Programmatically:
```xml
void setMask(String mask)
void setCharRepresentation(char charRepresentation)
void setMaskAndCharRepresentation(String mask, char charRepresentation)

void setShowMaskAsHint(boolean showMaskAsHint)

String getMask()
char getCharRepresentation()
boolean isShowMaskAsHint()

void clear()
```