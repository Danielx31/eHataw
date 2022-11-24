package com.danielx31.ehataw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class PolicyDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Privacy PolicyDialog")
                .setMessage("BSIT 4C Group 5 Capstone Team built the eHataw app as a Free app. This service is provided by BSIT 4C Group 5 Capstone Team at no cost and is intended for use as is.\n" +
                        "This page is used to inform visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.\n" +
                        "\n" +
                        "If you choose to use our service, then you agree to the collection and use of information in relation to this policy. The personal Information that we collect is used for providing and improving the service. We will not use or share your information with anyone except as described in this Privacy PolicyDialog.\n" +
                        "\n" +
                        "The terms used in this Privacy PolicyDialog have the same meanings as in our TermsFragment and Conditions, which are accessible at eHataw unless otherwise defined in this Privacy PolicyDialog.\n" +
                        "\n" +
                        "Information Collection and Use\n" +
                        "For a better experience while using our service, we may require you to provide us with certain personally identifiable information, including but not limited to Full Name. The information that we request will be retained by us and used as described in this Privacy PolicyDialog.\n" +
                        "\n" +
                        "The app does use third-party services that may collect information used to identify you. Link to the privacy policy of third-party service providers used by the app\n" +
                        "•\tGoogle Play Services\n" +
                        "\n" +
                        "Log Data\n" +
                        "We want to inform you that whenever you use our service, in a case of an error in the app we collect data and information (through third-party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.\n" +
                        "\n" +
                        "Cookies\n" +
                        "Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.\n" +
                        "\n" +
                        "This Service does not use these “cookies” explicitly. However, the app may use third-party code and libraries that use “cookies” to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.\n" +
                        "\n" +
                        "Service Providers\n" +
                        "We may employ third-party companies and individuals due to the following reasons:\n" +
                        "•\tTo facilitate our Service\n" +
                        "•\tTo provide the service on our behalf\n" +
                        "•\tTo perform Service-related services; or\n" +
                        "•\tTo assist us in analyzing how our service is used.\n" +
                        "We want to inform users of this service that these third parties have access to their personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                        "\n" +
                        "Security\n" +
                        "We value your trust in providing us your personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.\n" +
                        "\n" +
                        "Links to Other Sites\n" +
                        "This service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, we strongly advise you to review the Privacy PolicyDialog of these websites. We have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                        "\n" +
                        "Children’s Privacy\n" +
                        "These services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13 years of age. In the case we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that we will be able to do the necessary actions.\n" +
                        "\n" +
                        "Changes to This Privacy PolicyDialog\n" +
                        "We may update our Privacy PolicyDialog from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy PolicyDialog on this page.\n" +
                        "\n" +
                        "This policy is effective as of 2022-10-18\n" +
                        "\n" +
                        "Contact Us\n" +
                        "If you have any questions or suggestions about our Privacy PolicyDialog, do not hesitate to contact us at eHataw2022@gmail.com.\n" +
                        "\n" +
                        "This Privacy PolicyDialog page was created at privacypolicytemplate.net and modified/generated by App Privacy PolicyDialog Generator\n")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

}
