"""WeHealed URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.11/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf.urls import url, include
from django.contrib import admin
from django.conf.urls.static import static

from WeHealedAPI import views, api, flitto, welcome
from WeHealed import settings

urlpatterns = [
    #url(r'', welcome.home, name='index'),
    url(r'^admin/', admin.site.urls),
    url(r'^test/', views.detail),
    url(r'^api/substitution/', api.substitution),
    url(r'^api/test', api.test),
    url(r'^api/request_translate/', api.request_translate),
    url(r'^api/token', api.token),
    url(r'^api/translate', api.trans),
    url(r'^dbinit/', views.dbinit),
    url(r'^api/url_recommend', api.url_recommend),
    url(r'^api/update_recommend', api.update_recommend),
    url(r'^api/make_dataset', api.make_dataset),
    url(r'^api/numeric', api.get_numeric),
    url(r'^api/date', api.get_date),
    url(r'^api/rule', api.rule),
    url(r'^api/add_rule', api.add_rule),
    url(r'^api/flitto_receive_result', flitto.receive_result),
    url(r'^api/image_upload/', api.image_upload)
]

# media urlpattern setting
urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
