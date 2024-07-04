import { NgModule } from "@angular/core";
import { NativeHtmlService } from "./services/native-html.service";
import { UserInfoService } from "./services/userInfo.service";
import { StorageService } from "./services/storage.service";

@NgModule({
    providers: [
        NativeHtmlService,
        UserInfoService,
        StorageService
    ]
})
export class SharedModule {}