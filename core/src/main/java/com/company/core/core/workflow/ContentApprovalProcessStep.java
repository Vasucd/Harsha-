package com.company.core.core.workflow;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
        service = WorkflowProcess.class,
        property = "process.label=Company Content Approval Process Step"
)
public class ContentApprovalProcessStep implements WorkflowProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentApprovalProcessStep.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args)
            throws WorkflowException {
        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        if (StringUtils.isBlank(payloadPath)) {
            return;
        }

        Session session = workflowSession.adaptTo(Session.class);
        if (session == null) {
            throw new WorkflowException("Could not adapt workflow session to JCR session");
        }

        try {
            if (session.nodeExists(payloadPath + "/jcr:content")) {
                Node contentNode = session.getNode(payloadPath + "/jcr:content");
                contentNode.setProperty("approvalStatus", "approved");
                contentNode.setProperty("approvedByWorkflow", true);
                session.save();
                LOGGER.info("Workflow approval metadata added for {}", payloadPath);
            }
        } catch (Exception e) {
            throw new WorkflowException("Error in content approval process step", e);
        }
    }
}
